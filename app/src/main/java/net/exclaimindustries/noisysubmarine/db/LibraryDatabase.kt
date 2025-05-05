package net.exclaimindustries.noisysubmarine.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** The database that holds the song library(ies).  And the server configs. */
@Database(entities = [Server::class, ArtistEntity::class, AlbumEntity::class, SongEntity::class],
          version = 1)
@TypeConverters(Converters::class)
abstract class LibraryDatabase: RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        /**
         * Gets the handle to the database (a singleton).  Creates it if it isn't initialized yet.
         */
        fun getDatabase(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        LibraryDatabase::class.java,
                        "library_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun serverDao(): ServerDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
}