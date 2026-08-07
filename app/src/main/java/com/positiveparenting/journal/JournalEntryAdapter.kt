package com.positiveparenting.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.positiveparenting.R
import com.positiveparenting.data.JournalEntry
import java.time.ZoneId

/**
 * Renders one card per journal entry for the overview (A-2): timestamp, mood
 * emoji (if given), theme (A-5, if given), the day's prompt (if stored) and
 * the full entry text — entries are two to three sentences by design, so
 * nothing is truncated.
 *
 * Tapping a card reports back via [onEntryClick] so the theme can be added
 * later (A-5). Only the theme is editable; everything else stays read-only.
 */
class JournalEntryAdapter(private val onEntryClick: (JournalEntry) -> Unit) :
    ListAdapter<JournalEntry, JournalEntryAdapter.EntryViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(getItem(position), onEntryClick)
    }

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val timestampTextView: TextView = itemView.findViewById(R.id.entry_timestamp_textview)
        private val moodTextView: TextView = itemView.findViewById(R.id.entry_mood_textview)
        private val themeTextView: TextView = itemView.findViewById(R.id.entry_theme_textview)
        private val promptTextView: TextView = itemView.findViewById(R.id.entry_prompt_textview)
        private val textTextView: TextView = itemView.findViewById(R.id.entry_text_textview)

        fun bind(entry: JournalEntry, onEntryClick: (JournalEntry) -> Unit) {
            timestampTextView.text =
                EntryDateFormatter.format(entry.createdAtEpochMillis, ZoneId.systemDefault())
            textTextView.text = entry.text
            itemView.setOnClickListener { onEntryClick(entry) }

            // An unknown key (theme dropped from a later catalog) simply shows
            // nothing — the stored value stays untouched, nothing is discarded.
            val themeLabel = ThemeCatalog.indexOf(entry.theme)?.let { index ->
                itemView.context.resources
                    .getStringArray(R.array.theme_labels)
                    .getOrNull(index)
            }
            if (themeLabel == null) {
                themeTextView.visibility = View.GONE
            } else {
                themeTextView.visibility = View.VISIBLE
                themeTextView.text = themeLabel
            }

            val moodIndex = entry.mood?.minus(1)?.takeIf { it in moodEmojiIds.indices }
            if (moodIndex != null) {
                moodTextView.visibility = View.VISIBLE
                moodTextView.text = itemView.context.getString(moodEmojiIds[moodIndex])
                moodTextView.contentDescription =
                    itemView.context.getString(moodDescriptionIds[moodIndex])
            } else {
                moodTextView.visibility = View.GONE
            }

            if (entry.prompt.isNullOrBlank()) {
                promptTextView.visibility = View.GONE
            } else {
                promptTextView.visibility = View.VISIBLE
                promptTextView.text = entry.prompt
            }
        }

        private val moodEmojiIds = listOf(
            R.string.mood_1_emoji,
            R.string.mood_2_emoji,
            R.string.mood_3_emoji,
            R.string.mood_4_emoji,
            R.string.mood_5_emoji,
        )

        private val moodDescriptionIds = listOf(
            R.string.mood_1_description,
            R.string.mood_2_description,
            R.string.mood_3_description,
            R.string.mood_4_description,
            R.string.mood_5_description,
        )
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<JournalEntry>() {
            override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry) =
                oldItem == newItem
        }
    }
}
