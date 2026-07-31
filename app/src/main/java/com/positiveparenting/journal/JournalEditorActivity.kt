package com.positiveparenting.journal

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.positiveparenting.R
import com.positiveparenting.data.AppDatabase
import com.positiveparenting.data.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class JournalEditorActivity : AppCompatActivity() {

    private lateinit var entryEditText: TextInputEditText
    private lateinit var moodToggleGroup: MaterialButtonToggleGroup
    private lateinit var saveButton: MaterialButton

    private lateinit var prompt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_editor)

        val dateTextView: TextView = findViewById(R.id.date_textview)
        val promptTextView: TextView = findViewById(R.id.prompt_textview)
        entryEditText = findViewById(R.id.entry_text_edittext)
        moodToggleGroup = findViewById(R.id.mood_toggle_group)
        saveButton = findViewById(R.id.save_button)

        val today = LocalDate.now()
        dateTextView.text = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

        // The prompt is picked once when the screen opens and must not change during
        // the session (e.g. recreation across midnight), so it rides in the instance state.
        prompt = savedInstanceState?.getString(STATE_PROMPT)
            ?: PromptProvider.promptForDate(
                today,
                resources.getStringArray(R.array.daily_prompts).toList()
            )
        promptTextView.text = prompt

        savedInstanceState?.getInt(STATE_MOOD, NO_MOOD)?.let { mood ->
            moodButtonIdForMood(mood)?.let(moodToggleGroup::check)
        }

        entryEditText.doAfterTextChanged { text ->
            saveButton.isEnabled = !text.isNullOrBlank()
        }
        saveButton.isEnabled = !entryEditText.text.isNullOrBlank()

        saveButton.setOnClickListener { saveEntry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PROMPT, prompt)
        outState.putInt(STATE_MOOD, selectedMood() ?: NO_MOOD)
    }

    private fun saveEntry() {
        // Disable immediately so a double tap can't insert twice.
        saveButton.isEnabled = false
        val entry = JournalEntry(
            createdAtEpochMillis = System.currentTimeMillis(),
            text = entryEditText.text.toString(),
            mood = selectedMood(),
            prompt = prompt
        )
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext).journalEntryDao().insert(entry)
                }
                Toast.makeText(
                    this@JournalEditorActivity,
                    R.string.journal_editor_saved_confirmation,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                // Keep text and mood selection so nothing is lost; allow retrying.
                Toast.makeText(
                    this@JournalEditorActivity,
                    R.string.journal_editor_save_error,
                    Toast.LENGTH_LONG
                ).show()
                saveButton.isEnabled = true
            }
        }
    }

    private fun selectedMood(): Int? = when (moodToggleGroup.checkedButtonId) {
        R.id.mood_button_1 -> 1
        R.id.mood_button_2 -> 2
        R.id.mood_button_3 -> 3
        R.id.mood_button_4 -> 4
        R.id.mood_button_5 -> 5
        else -> null
    }

    private fun moodButtonIdForMood(mood: Int): Int? = when (mood) {
        1 -> R.id.mood_button_1
        2 -> R.id.mood_button_2
        3 -> R.id.mood_button_3
        4 -> R.id.mood_button_4
        5 -> R.id.mood_button_5
        else -> null
    }

    companion object {
        private const val STATE_PROMPT = "state_prompt"
        private const val STATE_MOOD = "state_mood"
        private const val NO_MOOD = 0
    }
}
