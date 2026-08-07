package com.positiveparenting.journal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.positiveparenting.R
import com.positiveparenting.data.AppDatabase
import com.positiveparenting.data.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The overview (A-2): all entries newest first — rereading is the point,
 * evaluation stays with the review (A-7). Reached from the editor; system
 * back returns there with the draft intact.
 *
 * A-5 softens A-2's read-only rule in exactly one place: tapping a card lets
 * the **theme** be added, changed or removed. Text, mood, prompt and
 * timestamp stay immutable — a journal one can rewrite is no longer a journal.
 */
class JournalOverviewActivity : AppCompatActivity() {

    private lateinit var entryAdapter: JournalEntryAdapter
    private lateinit var emptyTextView: TextView

    /** Guards against a second dialog while one is already open. */
    private var themeDialogOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_overview)

        emptyTextView = findViewById(R.id.overview_empty_textview)
        entryAdapter = JournalEntryAdapter(::showThemeDialog)
        findViewById<RecyclerView>(R.id.entries_recyclerview).apply {
            layoutManager = LinearLayoutManager(this@JournalOverviewActivity)
            adapter = entryAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        loadEntries()
    }

    private fun loadEntries() {
        lifecycleScope.launch {
            try {
                val entries = withContext(Dispatchers.IO) {
                    AppDatabase.get(this@JournalOverviewActivity)
                        .journalEntryDao()
                        .entriesNewestFirst()
                }
                entryAdapter.submitList(entries)
                emptyTextView.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(
                    this@JournalOverviewActivity,
                    R.string.journal_overview_load_error,
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    /**
     * Theme picker for an existing entry (A-5): the eight themes plus
     * "no theme" as the last option.
     */
    private fun showThemeDialog(entry: JournalEntry) {
        if (themeDialogOpen) return
        val labels = resources.getStringArray(R.array.theme_labels)
        val items = labels + getString(R.string.journal_overview_theme_none)
        val noneIndex = labels.size
        // No theme preselects "no theme"; a key this version does not know
        // starts without any selection rather than silently proposing to
        // overwrite it.
        val checkedIndex = when {
            entry.theme == null -> noneIndex
            else -> ThemeCatalog.indexOf(entry.theme) ?: -1
        }
        var selectedIndex = checkedIndex

        themeDialogOpen = true
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.journal_overview_theme_dialog_title)
            .setSingleChoiceItems(items, checkedIndex) { _, which -> selectedIndex = which }
            .setPositiveButton(R.string.journal_overview_theme_save) { _, _ ->
                when {
                    selectedIndex < 0 -> Unit
                    selectedIndex == noneIndex -> saveTheme(entry.id, null)
                    else -> ThemeCatalog.KEYS.getOrNull(selectedIndex)
                        ?.let { saveTheme(entry.id, it) }
                }
            }
            .setNegativeButton(R.string.journal_overview_theme_cancel, null)
            .setOnDismissListener { themeDialogOpen = false }
            .show()
    }

    private fun saveTheme(entryId: Long, theme: String?) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.get(this@JournalOverviewActivity)
                        .journalEntryDao()
                        .updateTheme(entryId, theme)
                }
                loadEntries()
            } catch (e: Exception) {
                // The list stays as it is; nothing half-written.
                Toast.makeText(
                    this@JournalOverviewActivity,
                    R.string.journal_overview_theme_error,
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}
