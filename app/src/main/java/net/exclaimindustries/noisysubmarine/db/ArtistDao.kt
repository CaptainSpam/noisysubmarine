package net.exclaimindustries.noisysubmarine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

/** The DAO for accessing artists. */
@Dao
interface ArtistDao {
    /** Add a bunch of artists to the database. */
    @Insert
    fun addArtists(artists: List<ArtistEntity>)

    /** Upsert a bunch of artists to the database.  This is generally what gets used during sync. */
    @Upsert
    fun upsertArtists(artists: List<ArtistEntity>)

    /**
     * Delete a bunch of artists from the database.  This is generally used during sync to clean up.
     */
    @Delete
    fun deleteArtists(artists: List<ArtistEntity>)

    /** Count the artists in a server. */
    @Query("SELECT COUNT(*) FROM artists WHERE serverId = :serverId")
    fun countArtistsForServer(serverId: Int): Int

    /** Count the artists in all servers. */
    @Query("SELECT COUNT(*) FROM artists")
    fun countArtistsInAllServers(): Int

    /** Get all the stored artists in a given server. */
    @Query("SELECT * FROM artists WHERE serverId = :serverId")
    fun getAllArtistsForServer(serverId: Int): List<ArtistEntity>

    /** Get a single artist by ID in a given server. */
    @Query("SELECT * FROM artists WHERE serverId = :serverId AND id = :id")
    fun getArtistForServerById(serverId: Int, id: String): ArtistEntity

    /** Get potentially multiple artists by a name search in a given server. */
    @Query("SELECT * FROM artists WHERE serverId = :serverId AND name LIKE :name")
    fun searchArtistsForServerByName(serverId: Int, name: String): List<ArtistEntity>

    /** Get all stored artists across all configured servers. */
    @Query("SELECT * FROM artists")
    fun getAllArtistsInAllServers(): List<ArtistEntity>

    /** Get potentially multiple artists by a name search across all configured servers. */
    @Query("SELECT * FROM artists WHERE name LIKE :name")
    fun searchArtistsInAllServersByName(name: String): List<ArtistEntity>
}