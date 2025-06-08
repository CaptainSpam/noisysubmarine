package net.exclaimindustries.noisysubmarine.db

import androidx.room.Entity

/**
 * A table of any songs, albums, or artists that are to be persisted on the device.  If a song ID is
 * in this table, it should be downloaded to the device as soon as convenient and NOT reaped to
 * reclaim space.  If an album or artist ID is in this table, all songs by that artist or in that
 * album should be downloaded similarly.
 */
@Entity(tableName = "persistence")
data class Persistence(
    /** ID of the server from which this thing comes. */
    val serverId: Int,
    /** ID of the thing being persisted. */
    val entityId: String,
    /** Type of thing being persisted. */
    val entityType: PersistenceType
)
