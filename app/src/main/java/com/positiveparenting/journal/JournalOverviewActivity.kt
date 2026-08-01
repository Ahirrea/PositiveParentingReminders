package com.positiveparenting.journal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.positiveparenting.R
import com.positiveparenting.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The overview (A-2): all entries newest first, read-only — rereading is the
 * point, evaluation stays with the review (A-7). Reached from the editor;
 * system back returns there with the draft intact.
 */
class JournalOverviewActivity : AppCompatActivity() {

    private lateinit var entryAdapter: JournalEntryAdapter
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_overview)

        emptyTextView = findViewById(R.id.overview_empty_textview)
        entryAdapter = JournalEntryAdapter()
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
}
