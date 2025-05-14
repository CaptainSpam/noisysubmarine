package net.exclaimindustries.noisysubmarine.db

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Various icon enums that can be used to represent a server.  These are intended to be very simple
 * icons, not, say, uploading entire bitmaps or whatnot.  Thus, the Material icons ought to do.
 */
enum class ServerIcon {
    NONE,
    HOME,
    CHECK,
    PERSON,
    DATE_RANGE,
    FAVORITE,
    BUILD,
    PLAY_ARROW,
    PHONE,
}

/** Gets the actual icon for a given ServerIcon enum, or null if NONE is given. */
fun getIconResource(input: ServerIcon): ImageVector? =
    when(input) {
        ServerIcon.NONE -> null
        ServerIcon.HOME -> Icons.Filled.Home
        ServerIcon.CHECK -> Icons.Filled.Check
        ServerIcon.PERSON -> Icons.Filled.Person
        ServerIcon.DATE_RANGE -> Icons.Filled.DateRange
        ServerIcon.FAVORITE -> Icons.Filled.Favorite
        ServerIcon.BUILD -> Icons.Filled.Build
        ServerIcon.PLAY_ARROW -> Icons.Filled.PlayArrow
        ServerIcon.PHONE -> Icons.Filled.Phone
    }