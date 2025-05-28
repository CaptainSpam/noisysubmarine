package net.exclaimindustries.noisysubmarine.opensubsonic

/** Exception thrown if there's an OpenSubsonic-level error with a request. */
class OpenSubsonicException(val error: ErrorEntity) : Exception(error.message) {
    override fun toString(): String {
        return "Error ${error.code}: ${error.message ?: "(no message)"}${if (error.helpUrl != null) " (${error.helpUrl})" else ""}"
    }
}