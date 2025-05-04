package net.exclaimindustries.noisysubmarine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

/** The DAO for accessing albums. */
@Dao
interface AlbumDao {
    /** Add a bunch of albums to the database. */
    @Insert
    fun addAlbums(albums: List<AlbumEntity>)

    /** Upsert a bunch of albums to the database.  This is generally what gets used during sync. */
    @Upsert
    fun upsertAlbums(albums: List<AlbumEntity>)

    /**
     * Delete a bunch of albums from the database.  This is generally used during sync to clean up.
     */
    @Delete
    fun deleteAlbums(albums: List<AlbumEntity>)

    /** Count the albums in a server. */
    @Query("SELECT COUNT(*) FROM albums WHERE serverId = :serverId")
    fun countAlbumsForServer(serverId: Int): Int

    /** Count the albums in all servers. */
    @Query("SELECT COUNT(*) FROM albums")
    fun countAlbumsInAllServers(): Int

    /** Get all the stored albums in a given server. */
    @Query("SELECT * FROM albums WHERE serverId = :serverId")
    fun getAllAlbumsForServer(serverId: Int): List<AlbumEntity>

    /** Get a single album by ID in a given server. */
    @Query("SELECT * FROM albums WHERE serverId = :serverId AND id = :id")
    fun getAlbumForServerById(serverId: Int, id: String): AlbumEntity

    /** Get potentially multiple albums by a name search in a given server. */
    @Query("SELECT * FROM albums WHERE serverId = :serverId AND name LIKE :name")
    fun searchAlbumsForServerByName(serverId: Int, name: String): List<AlbumEntity>

    /** Get all stored albums across all configured servers. */
    @Query("SELECT * FROM albums")
    fun getAllAlbumsInAllServers(): List<AlbumEntity>

    /** Get potentially multiple albums by a name search across all configured servers. */
    @Query("SELECT * FROM albums WHERE name LIKE :name")
    fun searchAlbumsInAllServersByName(name: String): List<AlbumEntity>

    /** Count the albums by a given artist in a given server. */
    @Query("SELECT COUNT(*) FROM albums WHERE serverId = :serverId AND artistId = :artistId")
    fun countAlbumsByArtist(serverId: Int, artistId: String): Int

    /** Get all albums by a given artist in a given server. */
    @Query("SELECT * FROM albums WHERE serverId = :serverId AND artistId = :artistId")
    fun getAlbumsByArtist(serverId: Int, artistId: String): List<AlbumEntity>
}