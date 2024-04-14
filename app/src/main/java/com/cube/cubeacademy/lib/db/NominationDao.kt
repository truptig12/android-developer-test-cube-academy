package com.cube.cubeacademy.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing nominations and nominees in the database.
 * This interface defines methods for performing database operations related to nominations and nominees,
 * such as inserting data and querying data in a relational manner.
 */
@Dao
interface NominationDao {

    /**
     * Inserts a list of nominations into the database.
     * If a nomination already exists (based on primary key conflict), it replaces the existing nomination.
     * This is useful for keeping the data fresh when updates occur in the source data.
     *
     * @param nomination List of Nomination objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNominations(nomination: List<Nomination>)

    /**
     * Inserts a list of nominees into the database.
     * Similar to insertAllNominations
     * @param nominee List of Nominee objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNominees(nominee: List<Nominee>)


    /**
     * Retrieves all nominations along with their associated nominees from the database.
     * @return A Flow emitting a list of NominationWithNominee objects.
     */
    @Transaction
    @Query("SELECT * FROM nomination")
    fun getNominationsWithNominees(): Flow<List<NominationWithNominee>>


    /**
     * Retrieves all nominees from the database.
     * @return A Flow emitting a list of Nominee objects.
     */
    @Query("SELECT * FROM nominee")
    fun getAllNominees(): Flow<List<Nominee>>
}