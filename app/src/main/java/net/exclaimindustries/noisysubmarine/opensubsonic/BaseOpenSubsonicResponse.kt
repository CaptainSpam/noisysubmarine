package net.exclaimindustries.noisysubmarine.opensubsonic

/** The statuses that can be returned from an OpenSubsonic request. */
enum class Status(val status: String) {
    /** All is fine, request was good. */
    OK("ok"),

    /** Something went wrong; an error response is expected. */
    FAILED("failed"),
}

/** The error codes that can be returned from a failed OpenSubsonic request. */
enum class ErrorCode(val code: Int) {
    /** A generic error. */
    GENERIC(0),
    /** Missing parameter in the request. */
    MISSING_PARAMETER(10),
    /** The client's protocol version is incompatible, and the **client** must upgrade. */
    INCOMPATIBLE_VERSION_CLIENT(20),
    /** The server's protocol version is incompatible, and the **server** must upgrade. */
    INCOMPATIBLE_VERSION_SERVER(30),
    /** The username or password (or password hash) are invalid. */
    BAD_LOGIN(40),
    /** Token authentication is not supported for LDAP users. */
    TOKEN_AUTH_NOT_SUPPORTED(41),
    /** The supplied auth mechanism (username/password or API key) is not supported. */
    AUTH_MECHANISM_NOT_SUPPORTED(42),
    /** The client supplied multiple conflicting auth methods. */
    CONFLICTING_AUTH_METHODS(43),
    /** The API key is invalid. */
    INVALID_API_KEY(44),
    /** Authentication was good, but the user is not authorized to do whatever was asked. */
    NOT_AUTHORIZED(50),
    /**
     * The server's trial period has expired and the server owner must purchase a proper Subsonic
     * license.  Obviously, this probably won't happen in an OpenSubsonic implementation.
     */
    TRIAL_EXPIRED(60),
    /** The requested thing was not found on the server. */
    NOT_FOUND(70),
    /**
     * The server returned some sort of bonkers invalid error code.  Hopefully the error message is
     * more helpful.
     */
    WAIT_WHAT(-1),
}

/** The data in an error response. */
data class ErrorEntity(
    /** The error code. */
    val code: ErrorCode,
    /** The error message (optional). */
    val message: String?,
    /** A URL pointing to hopefully helpful information regarding this error (optional). */
    val helpUrl: String?,
)

/** The basic data returned from any OpenSubsonic request that doesn't return binary data. */
data class BaseOpenSubsonicResponse(
    /** The status response. */
    val status: Status,
    /** The Subsonic protocol version.  Will likely be "1.16.1". */
    val version: String,
    /**
     * The type of server used.  Generally, this is the server software, like "Navidrome".  Will be
     * an empty string if this isn't OpenSubsonic.
     */
    val type: String,
    /** The server's version.  Will be an empty string if this isn't OpenSubsonic. */
    val serverVersion: String,
    /**
     * Whether or not this is an OpenSubsonic server (that is, false if this is a plain Subsonic
     * server).  In practice, most servers implementing anything Subsonic-related these days should
     * be OpenSubsonic.  If this is false, Noisy Submarine probably won't function correctly.
     */
    val openSubsonic: Boolean,
    /** The error, if status is FAILED.  Will be null if status is OK. */
    val error: ErrorEntity?,
)
