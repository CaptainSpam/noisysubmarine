package net.exclaimindustries.noisysubmarine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** The DAO for accessing songs. */
@Dao
interface SongDao {
    /** Add a bunch of songs to the database. */
    @Insert
    fun addSongs(songs: List<SongEntity>)

    /** Upsert a bunch of songs to the database.  This is generally what gets used during sync. */
    @Upsert
    fun upsertSongs(songs: List<SongEntity>)

    /**
     * Delete a bunch of songs from the database.  This is generally used during sync to clean up.
     */
    @Delete
    fun deleteSongs(songs: List<SongEntity>)

    /** Count the songs in a server. */
    @Query("SELECT COUNT(*) FROM songs WHERE serverId = :serverId")
    fun countSongsForServer(serverId: Int): Flow<Int>

    /** Count the songs in all servers. */
    @Query("SELECT COUNT(*) FROM songs")
    fun countSongsInAllServers(): Flow<Int>

    /** Get all the stored songs in a given server. */
    @Query("SELECT * FROM songs WHERE serverId = :serverId")
    fun getAllSongsForServer(serverId: Int): Flow<List<SongEntity>>

    /** Get a single song by ID in a given server. */
    @Query("SELECT * FROM songs WHERE serverId = :serverId AND id = :id")
    fun getSongForServerById(serverId: Int, id: String): SongEntity

    /** Get potentially multiple songs by a name search in a given server. */
    @Query("SELECT * FROM songs WHERE serverId = :serverId AND title LIKE :title")
    fun searchSongsForServerByName(serverId: Int, title: String): Flow<List<SongEntity>>

    /** Get all stored songs across all configured servers. */
    @Query("SELECT * FROM songs")
    fun getAllSongsInAllServers(): Flow<List<SongEntity>>

    /** Get potentially multiple songs by a name search across all configured servers. */
    @Query("SELECT * FROM songs WHERE title LIKE :title")
    fun searchSongsInAllServersByName(title: String): Flow<List<SongEntity>>

    /** Count the songs by a given artist in a given server. */
    @Query("SELECT COUNT(*) FROM songs WHERE serverId = :serverId AND artistId = :artistId")
    fun countSongsByArtist(serverId: Int, artistId: String): Flow<Int>

    /** Get all songs by a given artist in a given server. */
    @Query("SELECT * FROM songs WHERE serverId = :serverId AND artistId = :artistId")
    fun getSongsByArtist(serverId: Int, artistId: String): Flow<List<SongEntity>>

    /** Count the songs in a given album in a given server. */
    @Query("SELECT COUNT(*) FROM songs WHERE serverId = :serverId AND albumId = :albumId")
    fun countSongsInAlbum(serverId: Int, albumId: String): Flow<Int>

    /** Get all songs in a given album in a given server. */
    @Query("SELECT * FROM songs WHERE serverId = :serverId AND albumId = :albumId")
    fun getSongsInAlbum(serverId: Int, albumId: String): Flow<List<SongEntity>>
}