package com.cube.cubeacademy.lib.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee


/**
 * Represents the Room database for this application, which includes all of its entities and versions.
 *
 * @Database marks this class as a Room Database with a table (entity) of Nominee and Nomination.
 * @entities A list of all entities included in the database. Add more entities to the array as needed.
 * @version The version number of the database. Increment this number with each schema change.
 * @exportSchema A boolean indicating whether to export the database schema JSON files to your project folder.
 */
@Database(entities = [Nominee::class, Nomination::class], version = 1, exportSchema = false)
abstract class NominationDatabase : RoomDatabase() {

    abstract val nominationDao: NominationDao
}