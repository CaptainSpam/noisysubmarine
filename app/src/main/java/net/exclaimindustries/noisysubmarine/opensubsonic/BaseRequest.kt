package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri

/** The base of any call to an OpenSubsonic server. */
abstract class BaseRequest(val requestData: BaseRequestData) {
    /**
     * The base data for any call to an OpenSubsonic server.  This should be extended for most
     * requests, except maybe ping.
     *
     * @param server the base server Uri.  In general, this should just include the scheme, server,
     *               and port; all path info will be added by concrete implementations of
     *               BaseRequest
     * @param loginData the login data
     */
    data class BaseRequestData(val server: Uri, val loginData: LoginData)

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
        val builder = requestData.server.buildUpon()

        // Add the endpoint path.  Everything should be under /rest, with the endpoint name given by
        // implementation.
        builder.appendPath("rest").appendPath(getEndpoint())

        // The common base stuff.
        builder.appendQueryParameter("v", protocol)
        builder.appendQueryParameter("c", client)
        builder.appendQueryParameter("f", "json")

        // Add in the login data.
        requestData.loginData.addHttpParamsTo(builder)

        // Ready to go!
        return builder
    }

    /** Gets the endpoint name.  This should be a simple name, without "rest" before it. */
    protected abstract fun getEndpoint(): String

    /**
     * Executes the request.
     *
     * TODO: This should return something, I think?  Or at least be somehow threaded.
     */
    abstract fun execute()
}