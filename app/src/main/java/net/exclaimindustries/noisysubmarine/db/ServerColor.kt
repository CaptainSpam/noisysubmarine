package net.exclaimindustries.noisysubmarine.db

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Various color enums that can be used to represent a server.
 *
 * Look, we don't need to drag a dang color picker into this.
 */
enum class ServerColor {
    BLUE,
    RED,
    GREEN,
    YELLOW,
    CYAN,
    MAGENTA,
    ORANGE,
    LIGHT_BLUE,
    LIGHT_RED,
    LIGHT_GREEN,
    LIGHT_YELLOW,
    LIGHT_CYAN,
    LIGHT_MAGENTA,
    LIGHT_ORANGE,
    BLACK,
    GRAY,
    LIGHT_GRAY,
    WHITE,
}

// A whole mess of private consts so that we aren't calling Color.rgb() every time.
private const val BLUE_INT = 0xff1616fa.toInt()
private const val RED_INT = 0xfffa1616.toInt()
private const val GREEN_INT = 0xff16fa16.toInt()
private const val YELLOW_INT = 0xfffafa16.toInt()
private const val CYAN_INT = 0xff16fafa.toInt()
private const val MAGENTA_INT = 0xfffa16fa.toInt()
private const val ORANGE_INT = 0xfffa8816.toInt()
private const val LIGHT_BLUE_INT = 0xff7070fa.toInt()
private const val LIGHT_RED_INT = 0xfffa7070.toInt()
private const val LIGHT_GREEN_INT = 0xff70fa70.toInt()
private const val LIGHT_YELLOW_INT = 0xfffafa70.toInt()
private const val LIGHT_CYAN_INT = 0xff70fafa.toInt()
private const val LIGHT_MAGENTA_INT = 0xfffa70fa.toInt()
private const val LIGHT_ORANGE_INT = 0xfffab070.toInt()

/**
 * Resolves a server enum to an ARGB color int. We don't just make these the values of the enums
 * because we store the enum values in the database, and if we want to change the colors later, we
 * don't want stale colors sitting around.
 */
@ColorInt
fun getServerColor(input: ServerColor): Int =
    when (input) {
        ServerColor.BLUE -> BLUE_INT
        ServerColor.RED -> RED_INT
        ServerColor.GREEN -> GREEN_INT
        ServerColor.YELLOW -> YELLOW_INT
        ServerColor.CYAN -> CYAN_INT
        ServerColor.MAGENTA -> MAGENTA_INT
        ServerColor.ORANGE -> ORANGE_INT
        ServerColor.LIGHT_BLUE -> LIGHT_BLUE_INT
        ServerColor.LIGHT_RED -> LIGHT_RED_INT
        ServerColor.LIGHT_GREEN -> LIGHT_GREEN_INT
        ServerColor.LIGHT_YELLOW -> LIGHT_YELLOW_INT
        ServerColor.LIGHT_CYAN -> LIGHT_CYAN_INT
        ServerColor.LIGHT_MAGENTA -> LIGHT_MAGENTA_INT
        ServerColor.LIGHT_ORANGE -> LIGHT_ORANGE_INT
        ServerColor.BLACK -> Color.BLACK
        ServerColor.GRAY -> Color.GRAY
        ServerColor.LIGHT_GRAY -> Color.LTGRAY
        ServerColor.WHITE -> Color.WHITE
    }

/**
 * Returns whether an icon displayed against this ServerColor should use a light color (false) or a
 * dark color (true).
 */
fun usesDarkIconColor(color: ServerColor): Boolean =
    when(color) {
        ServerColor.BLUE,
        ServerColor.RED,
        ServerColor.GREEN,
        ServerColor.YELLOW,
        ServerColor.CYAN,
        ServerColor.MAGENTA,
        ServerColor.ORANGE,
        ServerColor.BLACK,
        ServerColor.GRAY -> false

        ServerColor.LIGHT_BLUE,
        ServerColor.LIGHT_RED,
        ServerColor.LIGHT_GREEN,
        ServerColor.LIGHT_YELLOW,
        ServerColor.LIGHT_CYAN,
        ServerColor.LIGHT_MAGENTA,
        ServerColor.LIGHT_ORANGE,
        ServerColor.LIGHT_GRAY,
        ServerColor.WHITE -> true
    }