package net.exclaimindustries.noisysubmarine.db

import androidx.room.Entity
import androidx.room.Index
import java.util.Date

/**
 * An album entity.  This corresponds to most of the data from an AlbumID3 response.  Anything
 * optional in the AlbumID3 definition may be null here, but not everything required is directly
 * present (i.e. songCount is not in this class, as it is derived from how many songs have a
 * specific albumId).
 *
 * At time of writing, this doesn't contain all the fun new stuff OpenSubsonic includes (i.e.
 * separating out original release date from a specific edition's release date).  That may be for a
 * future revision.
 */
@Entity(tableName = "albums",
        indices = [Index("name")],
        primaryKeys = ["id", "serverId"],
)
data class AlbumEntity(
    /** Album ID as it exists on the server. */
    val id: String,
    /** ID of the server from which this artist comes. */
    val serverId: Int,
    /** Name of the album. */
    val name: String,
    /** The album's artist ID */
    val artistId: String?,
    /** The album's coverArt ID (for fetching cover art from a getCoverArt call). */
    val coverArt: String?,
    /** Length of the album, in seconds. */
    val duration: Int,
    /** Number of times this album has been played. */
    val playCount: Long?,
    /** The time when this album was created on the server. */
    val created: Date,
    /** The time when this album was starred.  Null if not starred. */
    val starred: Date?,
    /** The release year of this album. */
    val year: Int?,
    /**
     * The genres in this album.  This corresponds to the genres field from AlbumID3, but may also
     * include the singular genre field if that doesn't exist in the plural genres.
     */
    val genres: List<String>,
    /** The album's MusicBrainz ID, if one exists. */
    val musicBrainzId: String?,
    /** The artist name to be displayed for this album, if different from what's in artistId. */
    val displayArtist: String?,
    /** The name of this album for sorting purposes (without articles, romanized, etc). */
    val sortName: String?,
    /** The explicitness of the album. */
    val explicitStatus: ExplicitStatus,
)
