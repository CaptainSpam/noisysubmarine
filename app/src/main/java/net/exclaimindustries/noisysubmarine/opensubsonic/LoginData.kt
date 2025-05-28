package net.exclaimindustries.noisysubmarine.opensubsonic

import android.net.Uri
import net.exclaimindustries.noisysubmarine.db.Server
import net.exclaimindustries.tools.MD5Tools
import java.security.SecureRandom

/**
 * An abstract class that contains login data for a user on an OpenSubsonic server.  This is used
 * in any REST call to add the appropriate params for login.
 */
abstract class LoginData {
    companion object {
        fun makeLoginDataForServer(server: Server): LoginData {
            return if (server.username !== null && server.password !== null && server.apiKey === null)
                SaltedPassword(server.username, server.password)
            else if (server.apiKey !== null && server.username === null && server.password === null)
                ApiKey(server.apiKey)
            else
                throw IllegalStateException("Server somehow has illegal login data: $server")
        }
    }

    /**
     * Adds HTTP params relevant to this LoginData to the given builder.  For instance, for salted
     * logins, this will generate salt, MD5 the password into it, and add both params (plus the
     * username).
     *
     * @param builder the Uri.Builder to modify
     */
    abstract fun addHttpParamsTo(builder: Uri.Builder)

    /**
     * A class representing the login data when using a username/salted password combo.  This will
     * handle generating random salt and hashing everything out.
     *
     * @param username the username
     * @param password the unsalted, unencoded password
     */
    class SaltedPassword(val username: String, val password: String): LoginData() {
        /** The characters we will be using for generating salt. */
        private val saltAlphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

        override fun addHttpParamsTo(builder: Uri.Builder) {
            builder.appendQueryParameter("u", username)

            val salt = generateSalt()
            builder.appendQueryParameter("s", salt)

            builder.appendQueryParameter("t", MD5Tools.mD5Hash("$password$salt"))
        }

        private fun generateSalt(length: Int = 8): String {
            // Let's use SecureRandom for this.  Why not?  This should be seeded in some way that's
            // at least good enough to generate salt.
            val random = SecureRandom.getInstance("NativePRNG")
            val salt = StringBuilder("")

            var i = 0
            while(i < length) {
                salt.append(saltAlphabet[random.nextInt(saltAlphabet.length - 1)])
                i++
            }

            return salt.toString()
        }
    }

    /**
     * A class representing the login data when using an API key.  There's no username here, it's
     * just a key.
     *
     * @param apiKey the API key
     */
    class ApiKey(val apiKey: String): LoginData() {
        override fun addHttpParamsTo(builder: Uri.Builder) {
            builder.appendQueryParameter("apiKey", apiKey)
        }
    }

    /**
     * A class representing the login data for a plain password.  That is, one where the password
     * will be set as a parameter in the URL.  In plaintext.
     *
     * Don't use this.  Just don't.  This is just here for completeness.
     *
     * @param username the username
     * @param password the unsalted, unencoded password
     */
    class PlainPassword(val username: String, val password: String) : LoginData() {
        override fun addHttpParamsTo(builder: Uri.Builder) {
            builder.appendQueryParameter("u", username)
            builder.appendQueryParameter("p", password)
        }
    }
}