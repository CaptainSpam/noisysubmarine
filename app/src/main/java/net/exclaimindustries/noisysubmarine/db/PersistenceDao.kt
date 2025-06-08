package net.exclaimindustries.noisysubmarine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** The DAO for accessing persistence data. */
@Dao
interface PersistenceDao {
    /** Add some amount of persistence data to the database. */
    @Insert
    fun addPersistence(persistences: List<Persistence>)

    /** Delete some amount of persistence data from the database. */
    @Delete
    fun deletePersistence(persistences: List<Persistence>)

    /** Get all persisted songs across all servers.  This is intended for the fetching service. */
    @Query("SELECT DISTINCT songs.* FROM songs, albums, artists, persistence WHERE persistence.serverId = songs.serverId AND ((persistence.entityType = 'song' AND songs.id = persistence.entityId) OR (persistence.entityType = 'album' AND songs.albumId = persistence.entityId) OR (persistence.entityType = 'artist' AND songs.artistId = persistence.entityId))")
    fun getAllPersistedSongs(): Flow<SongEntity>
}