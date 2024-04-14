package com.cube.cubeacademy.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import kotlinx.coroutines.flow.Flow

@Dao
interface NominationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNominations(nomination: List<Nomination>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNominees(nominee: List<Nominee>)

    @Transaction
    @Query("SELECT * FROM nomination")
    fun getNominationsWithNominees(): Flow<List<NominationWithNominee>>

    @Query("SELECT * FROM nominee")
    fun getAllNominees(): Flow<List<Nominee>>
}