package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri
import android.util.Log

/**
 * Pings the server and gets some basic server data back from it.
 */
class PingRequest(requestData: BaseRequestData) : BaseRequest(requestData) {
    companion object {
        private const val DEBUG_TAG: String = "PingRequest"
    }

    override val endpoint: String = "ping"

    // No additional params are needed for ping.
    override fun addParams(builder: Uri.Builder): Uri.Builder = builder

    /**
     * Executes the request.  Throws an exception if something goes wrong with the connection;
     * otherwise, returns a `BaseOpenSubsonicResponse`.
     */
    fun execute(): BaseOpenSubsonicResponse {
        Log.d(DEBUG_TAG, "Starting a ping fetch for: $requestData")
        val json = fetchDataAsJsonObject()

        return extractBaseResponse(json)
    }
}