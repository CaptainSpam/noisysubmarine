package net.exclaimindustries.noisysubmarine.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A server, complete with data needed to identify it and any server-specific options.  This data
 * doesn't come from the OpenSubsonic protocol, but is needed to keep library data from multiple
 * servers sorted out.
 */
@Entity(tableName = "servers",
        indices = [Index("url")],
)
data class Server(
    /** Server ID (automatic). */
    @PrimaryKey(autoGenerate = true) val id: Int,
    /**
     * The base URL of the server.  This should generally just be the scheme, server, and port,
     * without any endpoints or the "rest" path (though some servers may require more of a path
     * before it gets to "rest" anyway).  That is, something like "http://server.net:5000",
     * NOT "http://server.net:5000/rest".
     */
    val url: String,
    /** The server's human-friendly name. */
    val name: String,
    /** The username to log into the server.  Should be null if this server uses api keys. */
    val username: String?,
    /**
     * The password (unencrypted) to log into the server.  Should be null if this server uses api
     * keys.
     */
    val password: String?,
    /** The api key for the server.  Should be null if this server uses a username/password. */
    val apiKey: String?,
)
