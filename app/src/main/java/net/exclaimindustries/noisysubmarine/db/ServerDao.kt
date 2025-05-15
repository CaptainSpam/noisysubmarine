package net.exclaimindustries.noisysubmarine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** The DAO for accessing configured servers. */
@Dao
interface ServerDao {
    /** Add a server configuration to the database. */
    @Insert
    fun addServer(server: Server)

    /** Delete a server configuration from the database. */
    @Delete
    fun deleteServer(server: Server)

    /** Update a server configuration in the database. */
    @Update
    fun updateServer(server: Server)

    /** Get all the configured servers. */
    @Query("SELECT * FROM servers")
    fun getAllServers(): Flow<List<Server>>
}