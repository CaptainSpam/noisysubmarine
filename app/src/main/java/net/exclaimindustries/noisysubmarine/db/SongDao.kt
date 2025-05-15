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

    /** Sets the persist flag on a song. */
    @Query("UPDATE songs SET persisted = :flag WHERE serverId = :serverId AND id = :songId")
    fun persistSong(serverId: Int, songId: String, flag: Boolean)

    /**
     * Returns whether or not a given song is transitively persisted.  This will NOT return true if
     * the song is only DIRECTLY persisted but not TRANSITIVELY persisted; this is primarily for the
     * transitively-persisted icon on a song entry UI element (that is, an icon that shows the song
     * is persisted, but directly un-persisting it won't change this fact).
     */
    @Query("SELECT IIF(albumPersist OR artistPersist, 1, 0) FROM (SELECT COUNT(*) AS albumPersist FROM (SELECT albums.persisted FROM albums, songs WHERE songs.serverId=:serverId AND albums.serverId=:serverId AND songs.id=:songId AND songs.albumId=albums.id AND albums.persisted=1)),(SELECT COUNT(*) AS artistPersist FROM (SELECT artists.persisted FROM artists, songs WHERE songs.id=:songId AND songs.serverId=:serverId AND artists.serverId=:serverId AND songs.artistId=artists.id AND artists.persisted=1))")
    fun isSongStrictlyTransitivelyPersisted(serverId: Int, songId: String): Flow<Boolean>

    /**
     * Returns whether or not a given song is persisted, transitively or not.  This is for song
     * fetching purposes.
     */
    @Query("SELECT IIF(albumPersist OR artistPersist OR songPersist, 1, 0) FROM (SELECT COUNT(*) AS albumPersist FROM (SELECT albums.persisted FROM albums, songs WHERE songs.serverId=:serverId AND albums.serverId=:serverId AND songs.id=:songId AND songs.albumId=albums.id AND albums.persisted=1)),(SELECT COUNT(*) AS artistPersist FROM (SELECT artists.persisted FROM artists, songs WHERE songs.id=:songId AND songs.serverId=:serverId AND artists.serverId=:serverId AND songs.artistId=artists.id AND artists.persisted=1)), (SELECT COUNT(*) AS songPersist FROM songs WHERE songs.id=:songId AND songs.serverId=:serverId AND songs.persisted=1)")
    fun isSongTransitivelyPersisted(serverId: Int, songId: String): Flow<Boolean>
}