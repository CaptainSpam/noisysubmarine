package net.exclaimindustries.noisysubmarine.db

/** Enum that represents explicitness in an album or song. */
enum class ExplicitStatus(status: String) {
    /**
     * Either a song has no explicitness data set, or an album has no song with explicitness data.
     */
    NO_DATA(""),

    /**
     * Either a song is explicitly clean, or an album has at least one song explicitly clean and no
     * songs explicitly explicit.
     */
    CLEAN("clean"),

    /**
     * Either a song is explicitly explicit, or an album has at least one song explicitly explicit.
     */
    EXPLICIT("explicit")
}