package com.cube.cubeacademy.lib.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee

@Database(entities = [Nominee::class, Nomination::class], version = 1, exportSchema = false)
abstract class NominationDatabase : RoomDatabase() {

    abstract val nominationDao: NominationDao
}