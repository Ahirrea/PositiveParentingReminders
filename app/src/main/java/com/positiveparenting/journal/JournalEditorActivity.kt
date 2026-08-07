package com.positiveparenting.journal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.positiveparenting.R
import com.positiveparenting.data.AppDatabase
import com.positiveparenting.data.JournalEntry
import com.positiveparenting.reminder.ReminderScheduler
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The core loop of the product (A-1): today's prompt, a few sentences, an
 * optional mood, save — locally into Room (ADR-004), in under three minutes.
 */
class JournalEditorActivity : AppCompatActivity() {

    private lateinit var entryEditText: TextInputEditText
    private lateinit var moodToggleGroup: MaterialButtonToggleGroup
    private lateinit var themeChipGroup: ChipGroup
    private lateinit var saveButton: MaterialButton
    private lateinit var prompt: String

    // A denial is respected either way: the alarm stays armed, Android just
    // never shows the notification until the permission is granted in the
    // system settings (comfortable re-enabling comes with A-6).
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_editor)

        entryEditText = findViewById(R.id.entry_text_edittext)
        moodToggleGroup = findViewById(R.id.mood_toggle_group)
        themeChipGroup = findViewById(R.id.theme_chip_group)
        saveButton = findViewById(R.id.save_entry_button)
        buildThemeChips()

        val today = LocalDate.now()
        findViewById<TextView>(R.id.date_textview).text =
            today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

        // The prompt is fixed when the editor opens; across rotation and process
        // death the instance state keeps it stable past midnight.
        prompt = savedInstanceState?.getString(STATE_PROMPT)
            ?: PromptProvider.promptForDay(
                today.toEpochDay(),
                resources.getStringArray(R.array.daily_prompts).toList(),
            )
        findViewById<TextView>(R.id.prompt_textview).text = prompt

        savedInstanceState?.getInt(STATE_MOOD, 0)?.let { mood ->
            moodButtonIds.getOrNull(mood - 1)?.let(moodToggleGroup::check)
        }
        savedInstanceState?.getString(STATE_THEME)?.let(::checkThemeChip)

        entryEditText.doAfterTextChanged { updateSaveButtonState() }
        updateSaveButtonState()
        if (savedInstanceState == null) {
            entryEditText.requestFocus()
        }

        saveButton.setOnClickListener { saveEntry() }

        // Entry into the overview (A-2); the editor stays open so back
        // returns to the draft.
        findViewById<MaterialButton>(R.id.journal_overview_button).setOnClickListener {
            startActivity(Intent(this, JournalOverviewActivity::class.java))
        }

        // A-3: arm the daily reminder on every launch (idempotent — the
        // PendingIntent identity replaces the previous alarm) and ask for
        // the notification permission exactly once.
        ReminderScheduler.schedule(this)
        maybeRequestNotificationPermission()
    }

    private fun maybeRequestNotificationPermission() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) return
        val prefs = getSharedPreferences(REMINDER_PREFS, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_PERMISSION_REQUESTED, false)) return
        prefs.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    /**
     * One chip per theme (A-5), built from [ThemeCatalog.KEYS] paired with the
     * index-parallel `theme_labels` — the array stays the single source of the
     * list, the layout only carries the styling.
     */
    private fun buildThemeChips() {
        val labels = resources.getStringArray(R.array.theme_labels)
        val inflater = LayoutInflater.from(this)
        ThemeCatalog.KEYS.forEachIndexed { index, key ->
            val label = labels.getOrNull(index) ?: return@forEachIndexed
            val chip = inflater.inflate(R.layout.item_theme_chip, themeChipGroup, false) as Chip
            chip.id = View.generateViewId()
            chip.text = label
            chip.tag = key
            // The generated ids are not stable across process death; the theme
            // is restored by key below, so the framework must not restore
            // chip state from a stale id.
            chip.isSaveEnabled = false
            themeChipGroup.addView(chip)
        }
    }

    private fun checkThemeChip(key: String) {
        val chip = themeChipGroup.children().firstOrNull { it.tag == key } ?: return
        themeChipGroup.check(chip.id)
    }

    private fun selectedTheme(): String? {
        val checkedId = themeChipGroup.checkedChipId
        if (checkedId == View.NO_ID) return null
        return themeChipGroup.findViewById<Chip>(checkedId)?.tag as? String
    }

    private fun ChipGroup.children(): Sequence<Chip> =
        (0 until childCount).asSequence().mapNotNull { getChildAt(it) as? Chip }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PROMPT, prompt)
        outState.putInt(STATE_MOOD, selectedMood() ?: 0)
        outState.putString(STATE_THEME, selectedTheme())
    }

    private fun updateSaveButtonState() {
        saveButton.isEnabled = !entryEditText.text.isNullOrBlank()
    }

    private fun selectedMood(): Int? {
        val index = moodButtonIds.indexOf(moodToggleGroup.checkedButtonId)
        return if (index >= 0) index + 1 else null
    }

    private fun saveEntry() {
        val text = entryEditText.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return
        // Disable on the first tap so a double tap cannot insert twice.
        saveButton.isEnabled = false

        val entry = JournalEntry(
            createdAtEpochMillis = System.currentTimeMillis(),
            text = text,
            mood = selectedMood(),
            prompt = prompt,
            theme = selectedTheme(),
        )
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(this@JournalEditorActivity).journalEntryDao().insert(entry)
                }
                Toast.makeText(
                    this@JournalEditorActivity,
                    R.string.journal_editor_saved_message,
                    Toast.LENGTH_SHORT,
                ).show()
                finish()
            } catch (e: Exception) {
                // Keep text and mood; the user can just try again.
                Toast.makeText(
                    this@JournalEditorActivity,
                    R.string.journal_editor_save_error,
                    Toast.LENGTH_LONG,
                ).show()
                updateSaveButtonState()
            }
        }
    }

    private val moodButtonIds = listOf(
        R.id.mood_button_1,
        R.id.mood_button_2,
        R.id.mood_button_3,
        R.id.mood_button_4,
        R.id.mood_button_5,
    )

    companion object {
        private const val STATE_PROMPT = "state_prompt"
        private const val STATE_MOOD = "state_mood"
        private const val STATE_THEME = "state_theme"
        private const val REMINDER_PREFS = "reminder_prefs"
        private const val KEY_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
