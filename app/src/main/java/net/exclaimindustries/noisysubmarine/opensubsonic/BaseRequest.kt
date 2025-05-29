package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri
import android.util.Log
import net.exclaimindustries.noisysubmarine.db.Server
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/** The base of any call to an OpenSubsonic server. */
abstract class BaseRequest(open val requestData: BaseRequestData) {
    companion object {
        private const val DEBUG_TAG = "BaseRequest"

        /** Extracts the base response from OpenSubsonic. */
        @JvmStatic
        protected fun extractBaseResponse(json: JSONObject): BaseOpenSubsonicResponse {
            // First, find the subsonic-response container.
            val response = json.getJSONObject("subsonic-response")

            // Then, get some stuff out of it.
            return BaseOpenSubsonicResponse(status = if (response.optString("status") === "ok") Status.OK else Status.FAILED,
                                            version = response.getString("version"),
                                            type = response.optString("type", ""),
                                            serverVersion = response.optString("serverVersion", ""),
                                            openSubsonic = response.optBoolean("openSubsonic",
                                                                               false),
                                            error = try {
                                                val error = response.getJSONObject("error")

                                                ErrorEntity(code = try {
                                                    val code = error.getInt("code")

                                                    val codeEnum: ErrorCode? =
                                                        ErrorCode.entries.firstOrNull { it.code == code }
                                                    codeEnum ?: ErrorCode.WAIT_WHAT
                                                } catch (_: JSONException) {
                                                    ErrorCode.WAIT_WHAT
                                                },
                                                            message = error.optString("message"),
                                                            helpUrl = error.optString("helpUrl"))
                                            } catch (_: JSONException) {
                                                // Couldn't find an error, so it's null.
                                                null
                                            })
        }

        /**
         * Checks for an error in a response and throws an `OpenSubsonicException` if there is one.
         * Does nothing otherwise.
         */
        @JvmStatic
        protected fun throwOnError(json: JSONObject) {
            val response = extractBaseResponse(json)

            if (response.error != null) {
                Log.e(DEBUG_TAG, "Error in response: $response")
                throw OpenSubsonicException(response.error)
            }
        }
    }

    /**
     * The base data for any call to an OpenSubsonic server.  This should be extended for most
     * requests, except maybe ping.
     *
     * @param server the base Server object
     */
    open class BaseRequestData(val server: Server) {
        override fun toString(): String = "BaseRequestData(server=$server)"
    }

    /**
     * The Subsonic protocol.  Since this part of the protocol is based on the original Subsonic,
     * OpenSubsonic uses a different scheme, and the original Subsonic protocol hasn't been touched
     * in ages, this likely won't change in the near future.
     */
    private val protocol = "1.16.1"

    /**
     * The client name.  This should probably be a variable at some point, but for now it'll just be
     * hardcoded.
     */
    private val client = "Noisy Submarine"

    /**
     * The last-seen HTTP response code.  This is private so it can't be messed with outside of
     * `setLastResponseCode()`, which in turn is protected so outside callers can't mess with it
     * either.
     */
    private var lastResponseCode: Int? = null

    /**
     * Set the last response code received by an `HttpURLConnection` attempt.  This is generally
     * only useful if said response isn't 200, but set it on each connection anyway.
     *
     * @param code the last-seen code
     */
    protected fun setLastResponseCode(code: Int) { lastResponseCode = code }

    /**
     * Gets the response code received by the last connection attempt.  If all went well, this will
     * be 200.  If a connection error occurred, this won't be 200, and an exception was likely
     * thrown (which is probably why you're checking this method to begin with).  If no HTTP
     * connection could be made (i.e. an IOException happened before connecting or the request had
     * not been initiated yet), this will be null.
     */
    fun getLastResponseCode(): Int? = lastResponseCode

    /**
     * Creates a new `Uri.Builder` with the appropriate base data.  This should be the first step in
     * handling the request, and any request-specific params (that is, NOT the protocol version,
     * client name, login data, etc) should be added to the result.
     */
    protected fun makeBaseUriBuilder(): Uri.Builder {
        val builder = requestData.server.uri.buildUpon()

        // Add the endpoint path.  Everything should be under /rest, with the endpoint name given by
        // implementation.
        builder.appendPath("rest").appendPath(endpoint)

        // The common base stuff.
        builder.appendQueryParameter("v", protocol)
        builder.appendQueryParameter("c", client)
        builder.appendQueryParameter("f", "json")

        // Add in the login data.
        LoginData.makeLoginDataForServer(requestData.server).addHttpParamsTo(builder)

        // Ready to go!
        return builder
    }

    /**
     * Takes the base `Uri`, staples any needed params on it, connects, treats the entire response
     * as a `JSONObject`, and, assuming nothing went wrong that whole way, returns as such.  Note
     * that this returns the ENTIRE object; you'll most likely want to dig into the
     * `subsonic-response` object first (though if you're calling `extractBaseResponse`, that will
     * do that for you)
     */
    protected fun fetchDataAsJsonObject(): JSONObject {
        // Build the Uri.
        val uri = addParams(makeBaseUriBuilder()).build()

        var connection: HttpURLConnection? = null

        try {
            // Crack it open and let's go!
            connection = URL(uri.toString()).openConnection() as HttpURLConnection
            connection.connect()

            setLastResponseCode(connection.responseCode)
            if (lastResponseCode != 200) {
                Log.e(DEBUG_TAG, "Error response from server: $lastResponseCode")
                // Something else will get whatever happened here.
                throw IOException("Error response from server: $lastResponseCode")
            }

            // Hoover up that data!
            val br = BufferedReader(InputStreamReader(connection.inputStream))
            val buffer = StringBuffer()
            var line: String? = br.readLine()
            while (line != null) {
                buffer.append(line)
                line = br.readLine()
            }

            // With any luck, this should be a JSON blob.  If not, well, we've got an exception to
            // catch.
            val json = JSONObject(br.toString())
            throwOnError(json)

            return json
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Adds implementation-specific params to the given `Uri.Builder`.
     *
     * @param builder the input builder to modify
     * @return the same builder
     */
    protected abstract fun addParams(builder: Uri.Builder): Uri.Builder

    /** The endpoint name.  This should be a simple name, without "rest" before it. */
    protected abstract val endpoint: String
}