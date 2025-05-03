package net.exclaimindustries.tools

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * <code>MD5Tools</code> consists of a helper method for the common gruntwork tasks commonly
 * associated with MD5 hashing.  Most common of these would be the hashing of a simple string.  Yes,
 * yes, MD5 is broken and insecure and should not be used, blah blah blah, some existing protocols
 * use MD5 and will not be arbitrarily changed on your whims, so kindly shut your trap.
 *
 * For those keeping track at home, yes, this IS a direct Kotlin port of the MD5Tools class from
 * Geohash Droid, with relevant bits of CharToBytes thrown in as private functions for good measure.
 */
class MD5Tools {
    companion object {
        /**
         * Hashes a string through the MD5 algorithm. If something goes wrong with getting an MD5
         * instance, this returns an empty string.
         *
         * @param input
         *            String object to hash
         * @return the MD5 hash of the input
         */
        fun mD5Hash(input: String): String {
            var diggy: MessageDigest

            try {
                diggy = MessageDigest.getInstance("MD5")
            } catch (_: NoSuchAlgorithmException) {
                // If we don't have MD5 handy for some reason, this whole thing just falls apart.
                return ""
            }

            diggy.update(charsToBytes(input.toCharArray()))

            return bytesToString(diggy.digest())
        }

        private fun charsToBytes(chars: CharArray): ByteArray {
            val bytes = ByteArray(chars.size)
            var i = 0
            while (i < chars.size) {
                bytes[i] = (chars[i].code and 0xFF).toByte()
                i++
            }
            return bytes
        }

        private fun bytesToString(bytes: ByteArray): String {
            val sb = StringBuilder()
            var i = 0
            while (i < bytes.size) {
                if (i % 32 == 0 && i != 0) sb.append("\n")
                var s = bytes[i].toInt().toString(16)
                if (s.length < 2) s = "0$s"
                if (s.length > 2) s = s.substring(s.length - 2)
                sb.append(s)
                i++
            }
            return sb.toString()
        }

    }
}