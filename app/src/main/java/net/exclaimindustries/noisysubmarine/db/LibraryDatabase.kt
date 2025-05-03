package net.exclaimindustries.noisysubmarine.db

import androidx.room.Database
import androidx.room.TypeConverters

/** The database that holds the song library(ies). */
@Database(entities = [Server::class, ArtistEntity::class, AlbumEntity::class, SongEntity::class],
          version = 1)
@TypeConverters(Converters::class)
abstract class LibraryDatabase {
}