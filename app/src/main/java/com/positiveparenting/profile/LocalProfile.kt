package com.positiveparenting.profile

/**
 * The local profile decided in ADR-002 (option B): first names only, stored
 * on-device. Pure Kotlin — no Android types — so the input rules stay
 * JVM-testable.
 */
data class LocalProfile(
    val parentName: String,
    val childName: String? = null,
) {
    companion object {

        /**
         * Builds a profile from raw field input, or returns null when the
         * parent name is blank — the one thing the screen requires.
         */
        fun fromInput(parentInput: String, childInput: String): LocalProfile? {
            val parentName = normalizeName(parentInput)
            if (parentName.isEmpty()) return null
            val childName = normalizeName(childInput).ifEmpty { null }
            return LocalProfile(parentName, childName)
        }

        /** Mirrors [fromInput]'s requirement so the save button can track it live. */
        fun isValidParentName(input: String): Boolean = normalizeName(input).isNotEmpty()

        fun normalizeName(input: String): String =
            input.trim().replace(WHITESPACE_RUNS, " ")

        private val WHITESPACE_RUNS = Regex("\\s+")
    }
}
