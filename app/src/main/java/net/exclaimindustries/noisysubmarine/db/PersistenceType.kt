package net.exclaimindustries.noisysubmarine.db

/** Enum for storing the type of ID being persisted. */
enum class PersistenceType(type:String) {
    /** A song (the song with this ID should be persisted). */
    SONG("song"),
    /** An album (all songs with this ID as its album should be persisted). */
    ALBUM("album"),
    /** An artist (all songs with this ID as its artist should be persisted). */
    ARTIST("artist")
}