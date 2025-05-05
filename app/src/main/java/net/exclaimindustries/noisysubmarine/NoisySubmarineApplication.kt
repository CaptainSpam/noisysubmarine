package net.exclaimindustries.noisysubmarine

import android.app.Application
import net.exclaimindustries.noisysubmarine.db.LibraryDatabase

/** The application object.  This covers stuff that should be available application-wide. */
class NoisySubmarineApplication: Application() {
    /** It's the library database!  Let's have a big round of applause for the database! */
    val database by lazy { LibraryDatabase.getDatabase(this) }
}