package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri
import android.util.Log
import net.exclaimindustries.noisysubmarine.db.AlbumEntity
import net.exclaimindustries.noisysubmarine.db.ArtistEntity
import net.exclaimindustries.noisysubmarine.db.Converters
import net.exclaimindustries.noisysubmarine.db.ExplicitStatus
import net.exclaimindustries.noisysubmarine.db.Server
import net.exclaimindustries.noisysubmarine.db.SongEntity
import org.json.JSONException
import org.json.JSONObject
import java.util.Date

/**
 * Executes a search3 request.  While this will take enough params to do proper searches on the
 * server, Noisy Submarine is built around keeping a local copy of the entire song library in a
 * database, so most of the time this will be called with an empty query string.
 */
class Search3Request(override val requestData: Search3RequestData) : BaseRequest(requestData) {
    companion object {
        private const val DEBUG_TAG: String = "Search3Request"
    }

    override val endpoint: String = "search3"

    override fun addParams(builder: Uri.Builder): Uri.Builder =
        builder.appendQueryParameter("query", requestData.query)
                .appendQueryParameter("artistCount", requestData.artistCount.toString())
                .appendQueryParameter("artistOffset", requestData.artistOffset.toString())
                .appendQueryParameter("albumCount", requestData.albumCount.toString())
                .appendQueryParameter("albumOffset", requestData.artistOffset.toString())
                .appendQueryParameter("songCount", requestData.songCount.toString())
                .appendQueryParameter("songOffset", requestData.songOffset.toString())

    /**
     * The data and params a search3 uses.  Use an empty query string to get everything the server
     * has (limited by artist/album/songCount).
     *
     * @param server the base Server object
     * @param query the query string
     * @param artistCount the max number of artists to retrieve
     * @param artistOffset the offset at which to start retrieving artists (for pagination)
     * @param albumCount the max number of albums to retrieve
     * @param albumOffset the offset at which to start retrieving albums (for pagination)
     * @param songCount the max number of songs to retrieve
     * @param songOffset the offset at which to start retrieving songs (for pagination)
     */
    class Search3RequestData(server: Server,
                             val query: String = "",
                             val artistCount: Int = 100,
                             val artistOffset: Int = 0,
                             val albumCount: Int = 100,
                             val albumOffset: Int = 0,
                             val songCount: Int = 100,
                             val songOffset: Int = 0) : BaseRequestData(server) {
        override fun toString(): String =
            "Search3RequestData(server=$server, query=$query, artistCount=$artistCount, artistOffset=$artistOffset, albumCount=$albumCount, artistOffset=$artistOffset, songCount=$songCount, songOffset=$songOffset)"
    }

    /** The results of a search. */
    data class Search3ResponseData(val artists: List<ArtistEntity>,
                                   val albums: List<AlbumEntity>,
                                   val songs: List<SongEntity>)

    /**
     * Executes the request.
     *
     * TODO: Handle JSONExceptions in parsing; it shouldn't stop everything, but we should be able
     * to report this to the caller somehow.
     */
    fun execute(): Search3ResponseData {
        Log.d(DEBUG_TAG, "Starting a search3 fetch for: $requestData")
        val json = fetchDataAsJsonObject()

        val subsonic = json.getJSONObject("subsonic-response")
        val results = subsonic.getJSONObject("searchResult3")

        val artists = ArrayList<ArtistEntity>()
        val albums = ArrayList<AlbumEntity>()
        val songs = ArrayList<SongEntity>()

        if (results.has("artist")) {
            val artistJsonArray = results.getJSONArray("artist")
            for (i in 0..artistJsonArray.length() - 1) {
                artists.add(parseArtist(artistJsonArray.getJSONObject(i)))
            }
        }

        if (results.has("album")) {
            val albumJsonArray = results.getJSONArray("album")
            for (i in 0..albumJsonArray.length() - 1) {
                albums.add(parseAlbum(albumJsonArray.getJSONObject(i)))
            }
        }

        if (results.has("song")) {
            val songJsonArray = results.getJSONArray("song")
            for (i in 0..songJsonArray.length() - 1) {
                songs.add(parseSong(songJsonArray.getJSONObject(i)))
            }
        }

        Log.d(DEBUG_TAG, "Done.  Artists: ${artists.size}; Albums: ${albums.size}; Songs: ${songs.size}")
        return Search3ResponseData(artists = artists, albums = albums, songs = songs)
    }

    /** Convenience function because optString doesn't allow for nulls as the fallback. */
    private fun getStringOrNull(jsonObj: JSONObject, field: String): String? =
        if (jsonObj.has(field)) jsonObj.getString(field) else null

    /** Convenience function because optLong doesn't allow for nulls as the fallback. */
    private fun getLongOrNull(jsonObj: JSONObject, field: String): Long? =
        if (jsonObj.has(field)) jsonObj.getLong(field) else null

    /** Convenience function because optInt doesn't allow for nulls as the fallback. */
    private fun getIntOrNull(jsonObj: JSONObject, field: String): Int? =
        if (jsonObj.has(field)) jsonObj.getInt(field) else null

    /** Convenience function because optString doesn't allow for nulls as the fallback. */
    private fun getDateOrNull(jsonObj: JSONObject, field: String): Date? =
        if (jsonObj.has(field)) Converters.convertIso8601ToDate(jsonObj.getString(field)) else null

    /**
     * Parses an OpenSubsonic ArtistID3 object into an ArtistEntity.  This does very little error
     * checking; if the server is returning bogus data (i.e. fields that are strictly required are
     * missing), this will throw an exception.  Optional fields should be okay.
     *
     * Remember, the `persisted` field is just a placeholder here; if this is updating an existing
     * entry, it needs to be changed to whatever's in the database already.
     */
    private fun parseArtist(artistJson: JSONObject): ArtistEntity =
        ArtistEntity(serverId = requestData.server.id,
                     id = artistJson.getString("id"),
                     name = artistJson.getString("name"),
                     coverArt = getStringOrNull(artistJson, "coverArt"),
                     artistImageUrl = getStringOrNull(artistJson, "artistImageUrl"),
                     starred = getDateOrNull(artistJson, "starred"),
                     musicBrainzId = getStringOrNull(artistJson, "musicBrainzId"),
                     sortName = getStringOrNull(artistJson, "sortName"),
                     persisted = false)

    /**
     * Parses an OpenSubsonic AlbumID3 object into an AlbumEntity.  This does very little error
     * checking; if the server is returning bogus data (i.e. fields that are strictly required are
     * missing), this will throw an exception.  Optional fields should be okay.
     *
     * Remember, the `persisted` field is just a placeholder here; if this is updating an existing
     * entry, it needs to be changed to whatever's in the database already.
     */
    private fun parseAlbum(albumJson: JSONObject): AlbumEntity {
        // Genres need to be handled weirdly.
        val genres = HashSet<String>()

        // First, the singular genre, if it exists.
        if (albumJson.has("genre")) genres.add(albumJson.getString("genre"))

        // Then, the plural genres, if they exist.
        try {
            val genreJson = albumJson.getJSONArray("genres")
            for (i in 0..genreJson.length() - 1) {
                genres.add(genreJson.getString(i))
            }
        } catch (_: JSONException) {
            // Ignore this, there's no genres (plural).
        }

        return AlbumEntity(serverId = requestData.server.id,
                           id = albumJson.getString("id"),
                           name = albumJson.getString("name"),
                           artistId = getStringOrNull(albumJson, "artistId"),
                           coverArt = getStringOrNull(albumJson, "coverArt"),
                           duration = albumJson.getInt("duration"),
                           playCount = getLongOrNull(albumJson, "playCount"),
                           created = Converters.convertIso8601ToDate(albumJson.getString("created")),
                           starred = getDateOrNull(albumJson, "starred"),
                           year = getIntOrNull(albumJson, "year"),
                           genres = genres.toList(),
                           musicBrainzId = getStringOrNull(albumJson, "musicBrainzId"),
                           displayArtist = getStringOrNull(albumJson, "displayArtist"),
                           sortName = getStringOrNull(albumJson, "sortName"),
                           explicitStatus = ExplicitStatus.valueOf(albumJson.optString("explicitStatus",
                                                                                       "")),
                           persisted = false
        )
    }

    /**
     * Parses an OpenSubsonic Child object into a SongEntity.  This does very little error checking;
     * if the server is returning bogus data (i.e. fields that are strictly required are missing),
     * this will throw an exception.  Optional fields should be okay.
     *
     * Remember, the `persisted` field is just a placeholder here; if this is updating an existing
     * entry, it needs to be changed to whatever's in the database already.
     */
    private fun parseSong(songJson: JSONObject): SongEntity {
        // Genres need to be handled weirdly.
        val genres = HashSet<String>()

        // First, the singular genre, if it exists.
        if (songJson.has("genre")) genres.add(songJson.getString("genre"))

        // Then, the plural genres, if they exist.
        try {
            val genreJson = songJson.getJSONArray("genres")
            for (i in 0..genreJson.length() - 1) {
                genres.add(genreJson.getString(i))
            }
        } catch (_: JSONException) {
            // Ignore this, there's no genres (plural).
        }

        return SongEntity(serverId = requestData.server.id,
                          id = songJson.getString("id"),
                          title = songJson.getString("title"),
                          albumId = getStringOrNull(songJson, "albumId"),
                          artistId = getStringOrNull(songJson, "artistId"),
                          track = getIntOrNull(songJson, "track"),
                          coverArt = getStringOrNull(songJson, "coverArt"),
                          size = songJson.getInt("size"),
                          contentType = getStringOrNull(songJson, "contentType"),
                          suffix = getStringOrNull(songJson, "suffix"),
                          duration = songJson.getInt("duration"),
                          bitRate = getIntOrNull(songJson, "bitRate"),
                          bitDepth = getIntOrNull(songJson, "bitDepth"),
                          samplingRate = getIntOrNull(songJson, "samplingRate"),
                          channelCount = getIntOrNull(songJson, "channelCount"),
                          playCount = getLongOrNull(songJson, "playCount"),
                          discNumber = getIntOrNull(songJson, "discNumber"),
                          created = Converters.convertIso8601ToDate(songJson.getString("created")),
                          starred = getDateOrNull(songJson, "starred"),
                          comment = getStringOrNull(songJson, "comment"),
                          genres = genres.toList(),
                          musicBrainzId = getStringOrNull(songJson, "musicBrainzId"),
                          sortName = getStringOrNull(songJson, "sortName"),
                          explicitStatus = ExplicitStatus.valueOf(songJson.optString("explicitStatus",
                                                                                     "")),
                          persisted = false
        )
    }
}