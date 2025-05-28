package net.exclaimindustries.noisysubmarine.opensubsonic

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Pings the server and gets some basic server data back from it.
 */
class PingRequest(requestData: BaseRequestData) : BaseRequest(requestData) {
    companion object {
        private const val DEBUG_TAG: String = "PingRequest"
    }

    override val endpoint: String = "ping"

    private var lastResponseCode: Int? = null

    /**
     * Executes the request.  Throws an exception if something goes wrong with the connection;
     * otherwise, returns a BaseOpenSubsonicResponse.
     */
    fun execute(): BaseOpenSubsonicResponse {
        val uri = makeBaseUriBuilder().build()

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
            return throwOnError(extractBaseResponse(json))
        } finally {
            connection?.disconnect()
        }
    }
}