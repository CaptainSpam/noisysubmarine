package net.exclaimindustries.noisysubmarine.db

import androidx.room.Entity
import androidx.room.Index
import java.util.Date

/**
 * An artist entity.  This corresponds to some of the data from an ArtistID3 response.  Anything
 * optional in the ArtistID3 definition may be null here, but not everything required is directly.
 * present (i.e. albumCount is not in this class, as it is derived from how many albums have a
 * specific artistId).
 */
@Entity(tableName = "artists",
        indices = [Index("name")],
        primaryKeys = ["id", "serverId"],
)
data class ArtistEntity(
    /** Artist ID as it exists on the server. */
    val id: String,
    /** ID of the server from which this artist comes. */
    val serverId: Int,
    /** Name of the artist. */
    val name: String,
    /** The artist's coverArt ID (for fetching cover art from a getCoverArt call). */
    val coverArt: String?,
    /** A URL to an artist image (for NOT fetching cover art from a getCoverArt call, I guess?). */
    val artistImageUrl: String?,
    /** The time this artist was starred.  Null if not starred. */
    val starred: Date?,
    /** The artist's MusicBrainz ID, if one exists. */
    val musicBrainzId: String?,
    /** The name of this artist for sorting purposes (without articles, romanized, etc). */
    val sortName: String?,
)
