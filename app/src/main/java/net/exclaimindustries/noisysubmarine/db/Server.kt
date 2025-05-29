package net.exclaimindustries.noisysubmarine.db

import android.net.Uri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

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
     * The base URI of the server.  This should generally just be the scheme, server, and port,
     * without any endpoints or the "rest" path (though some servers may require more of a path
     * before it gets to "rest" anyway).  That is, something like "http://server.net:5000",
     * NOT "http://server.net:5000/rest"; the endpoints and params will be added to this.
     */
    val uri: Uri,
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
    /**
     * The last time the local database was synced with the remote.  Will be null if there hasn't
     * been a sync yet.  If I ever find a "give me all changes since X date" function in
     * OpenSubsonic, this will sure be useful.
     */
    val lastSynced: Date?,
    /** The color used to represent this server. */
    val color: ServerColor,
    /**
     * The icon used to represent this server.  If this is anything other than NONE, this icon gets
     * drawn into areas where a server's color is placed, to help with users who may have a hard
     * time telling colors apart (and have multiple servers configured).  Or, y'know, if a user just
     * wants one of these icons in the interface.
     */
    val icon: ServerIcon,
) {
    // Overriding this so as not to display the password or API key in logs.  It's called being
    // responsible.
    override fun toString(): String = "Server(uri=$uri, name=$name, username=$username, password=${if(password !== null) "(defined)" else "null"}, apiKey=${if(apiKey !== null) "(defined)" else "null"}, lastSynced=$lastSynced, color=$color, icon=$icon)"
}
