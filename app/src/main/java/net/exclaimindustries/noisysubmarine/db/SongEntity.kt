package net.exclaimindustries.noisysubmarine.db

import androidx.room.Entity
import androidx.room.Index
import java.util.Date

/**
 * A song entity.  This kinda-sorta corresponds to a Child response, pared down to just data that
 * could be related to a song (OpenSubsonic's protocol allows for the potential for video data).  As
 * this app was originally designed for music and that's it, this may ignore a lot of what's in
 * Child.
 *
 * The data here reflects the song as it appears in the remote library, *before any transcoding*.
 * Don't count on it being accurate if the user has transcoded things locally for smaller file sizes
 * or reduced data consumption.
 */
@Entity(tableName = "songs",
        indices = [Index("name")],
        primaryKeys = ["id", "serverId"],
)
data class SongEntity(
    /** Song ID as it exists on the server. */
    val id: String,
    /** ID of the server from which this song comes. */
    val serverId: Int,
    /** Title of the song. */
    val title: String,
    /** The song's album ID. */
    val albumId: String?,
    /** The song's artist ID. */
    val artistId: String?,
    /** The track number. */
    val track: Int?,
    /** The song's coverArt ID (for fetching cover art from a getCoverArt call). */
    val coverArt: String?,
    /** The size of the song, in bytes. */
    val size: Int,
    /** Content-Type of the song file (before any transcoding). */
    val contentType: String?,
    /** Suffix of the song file name (before any transcoding). */
    val suffix: String?,
    /** Length of the song, in seconds. */
    val duration: Int,
    /** The bitrate of the song (before any transcoding). */
    val bitRate: Int?,
    /** The bit depth of the song (before any transcoding). */
    val bitDepth: Int?,
    /** The sampling rate of the song (before any transcoding). */
    val samplingRate: Int?,
    /** The channel count of the song (before any transcoding). */
    val channelCount: Int?,
    /** Number of times this song has been played. */
    val playCount: Long?,
    /** Disc number of this track, if this is from a multi-disc album. */
    val discNumber: Int?,
    /** The time when this song was created on the server. */
    val created: Date,
    /** The time when this song was starred.  Null if not starred. */
    val starred: Date?,
    /** The comment field on this song. */
    val comment: String?,
    /**
     * The genres of this song.  This corresponds to the genres field from Child, but may also
     * include the singular genre field if that doesn't exist in the plural genres.
     */
    val genres: List<String>,
    /** The song's MusicBrainz ID, if one exists. */
    val musicBrainzId: String?,
    /** The name of this song for sorting purposes (without articles, romanized, etc). */
    val sortName: String?,
    /** The explicitness of the song. */
    val explicitStatus: ExplicitStatus,
)
