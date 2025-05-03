package net.exclaimindustries.noisysubmarine.opensubsonic

/**
 * Pings the server and gets some basic server data back from it.
 */
class PingRequest(requestData: BaseRequestData): BaseRequest(requestData) {
    override fun getEndpoint(): String = "ping"

    override fun execute() {
        val builder = makeBaseUriBuilder()

        // Okay, now do something with it.
    }
}