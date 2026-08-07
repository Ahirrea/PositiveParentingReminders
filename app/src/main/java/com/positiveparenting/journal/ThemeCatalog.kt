package com.positiveparenting.journal

/**
 * The fixed list of themes an entry can be tagged with (A-5).
 *
 * Stored in the database is the **stable key**, never the German label: the
 * wording may be changed at any time without devaluing the history — `anger`
 * stays `anger` even if its label becomes something else. The labels live
 * index-parallel to [KEYS] in the `theme_labels` array in strings.xml; that
 * parallelism is the one fragile part of this design and is covered by
 * `ThemeLabelsTest`.
 *
 * Pure Kotlin on purpose: the keys are the values that end up on disk, so
 * they are worth a JVM test of their own.
 */
object ThemeCatalog {

    /**
     * Eight themes — enough to cover the everyday, few enough that four weeks
     * of entries still produce countable numbers for the review (A-7).
     * The order is the display order and must match `theme_labels`.
     */
    val KEYS: List<String> = listOf(
        "bedtime",
        "morning",
        "meals",
        "siblings",
        "anger",
        "screentime",
        "chores",
        "closeness",
    )

    /** Position in [KEYS], or null for null and for keys this version does not know. */
    fun indexOf(key: String?): Int? {
        if (key == null) return null
        return KEYS.indexOf(key).takeIf { it >= 0 }
    }

    /**
     * Whether [key] belongs to the current catalog. A stored key that is no
     * longer listed stays untouched in the database — it is simply not shown.
     */
    fun isKnown(key: String?): Boolean = indexOf(key) != null
}
