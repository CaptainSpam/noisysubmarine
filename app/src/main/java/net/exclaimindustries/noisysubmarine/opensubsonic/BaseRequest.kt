package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri
import android.util.Log
import net.exclaimindustries.noisysubmarine.db.Server
import org.json.JSONException
import org.json.JSONObject

/** The base of any call to an OpenSubsonic server. */
abstract class BaseRequest(val requestData: BaseRequestData) {
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
         * Checks for an error in a response and throws an OpenSubsonicException if there is one.
         * Otherwise, returns the input response.
         */
        @JvmStatic
        protected fun throwOnError(response: BaseOpenSubsonicResponse): BaseOpenSubsonicResponse {
            if (response.error != null) {
                Log.e(DEBUG_TAG, "Error in response: $response")
                throw OpenSubsonicException(response.error)
            }

            return response
        }
    }

    /**
     * The base data for any call to an OpenSubsonic server.  This should be extended for most
     * requests, except maybe ping.
     *
     * @param server the base Server object
     */
    data class BaseRequestData(val server: Server)

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
     * Creates a new Uri.Builder with the appropriate base data.  This should be the first step in
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

    /** The endpoint name.  This should be a simple name, without "rest" before it. */
    protected abstract val endpoint: String
}