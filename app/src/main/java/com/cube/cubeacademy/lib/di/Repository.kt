package com.cube.cubeacademy.lib.di

import android.database.sqlite.SQLiteException
import android.util.Log
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.db.NominationDao
import com.cube.cubeacademy.lib.db.NominationWithNominee
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.AndroidLogger
import com.cube.cubeacademy.utils.Logger
import com.cube.cubeacademy.utils.UiEvent
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

/**
 * @param api The API service interface for making network requests.
 * @param nominationDao The DAO for accessing the Room database for nominations. This DAO abstracts
 * away SQLite operations with a more convenient API and compile-time checked queries.
 * @param logger A utility for logging errors and information, crucial for debugging and monitoring the application's behavior.
 */
class Repository(
    val api: ApiService,
    private val nominationDao: NominationDao,
    private val logger: AndroidLogger = AndroidLogger()
) {

    /**
     * Retrieves a live list of nominations with their corresponding nominees.
     * Utilizes Flow to ensure the UI updates reactively to database changes.
     *
     * @return Flow of NominationWithNominee objects reflecting real-time data.
     */
    fun getNominationsWithNominees(): Flow<List<NominationWithNominee>> {
        return nominationDao.getNominationsWithNominees()
    }

    fun getNominees(): Flow<List<Nominee>> {
        return nominationDao.getAllNominees()
    }

    /**
     * Central method for refreshing all nomination-related data from the network and updating the local database.
     * simplifying the management of data synchronization
     */
    suspend fun refreshData() {

        getAllNominations()
        getAllNominees()
    }

    /**
     * Fetches nominations from the API and updates the local database, allowing unit testing and error handling.
     * Errors are logged with specific tags, helping differentiate the sources of issues in a complex system.
     */
    suspend fun getAllNominations() {
        try {
            val nominationsResponse = api.getAllNominations()
            nominationsResponse.data.let {
                nominationDao.insertAllNominations(it)
            }
        } catch (e: HttpException) {
            logger.error("HttpException", "Error refreshing data: ${e.message}")
        } catch (e: IOException) {
            logger.error("IOException", "Error refreshing data: ${e.message}")
        } catch (e: SQLiteException) {
            logger.error("SQLiteException", "Error refreshing data: ${e.message}")
        } catch (e: Exception) {
            logger.error("Exception", e.message.toString())
        }
    }


    /**
     *Similar to getAllNominations, this method fetches and updates nominees.
     * */
    suspend fun getAllNominees() {
        try {
            val nomineesResponse = api.getAllNominees()
            nomineesResponse.data.let {
                nominationDao.insertAllNominees(it)
            }
        } catch (e: HttpException) {
            logger.error("HttpException", "Error refreshing data: ${e.message}")
        } catch (e: IOException) {
            logger.error("IOException", "Error refreshing data: ${e.message}")
        } catch (e: SQLiteException) {
            logger.error("SQLiteException", "Error refreshing data: ${e.message}")
        } catch (e: Exception) {
            logger.error("Exception", e.message.toString())
        }
    }

    suspend fun createNomination(
        nomineeId: String,
        reason: String,
        process: String
    ): UiEvent<Nomination> {
        return try {
            val response = api.createNomination(nomineeId, reason, process)
            response.data?.let {
                UiEvent.Success(it)
            } ?: UiEvent.Error("No data returned from the server")
        } catch (e: Exception) {
            UiEvent.Error(e.message ?: "An error occurred during nomination creation")
        }
    }

}