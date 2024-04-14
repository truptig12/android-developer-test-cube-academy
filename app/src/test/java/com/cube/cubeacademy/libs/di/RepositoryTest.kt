package com.cube.cubeacademy.libs.di

import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.db.NominationDao
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.DataWrapper
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.AndroidLogger
import com.cube.cubeacademy.utils.UiEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


@ExperimentalCoroutinesApi
class RepositoryTest {

    private lateinit var repository: Repository
    private lateinit var api: ApiService
    private lateinit var nominationDao: NominationDao
    private val logger = mockk<AndroidLogger>(relaxed = true)

    @Before
    fun setup() {
        api = mockk()
        nominationDao = mockk(relaxed = true)
        repository = Repository(api, nominationDao,logger)
    }

    @Test
    fun `refreshData handles HttpException and logs appropriately`() = runTest {
        // Prepare
        val httpException = HttpException(Response.error<Any>(404, mockk(relaxed = true)))
        coEvery { api.getAllNominations() } throws httpException
        coEvery { api.getAllNominees() } returns DataWrapper(emptyList())

        // Act
        repository.refreshData()

        // Assert
        coVerify { logger.error("HttpException", match { it.contains("Error refreshing data") }) }
    }

    @Test
    fun `refreshData handles IOException and logs appropriately`() = runTest {
        // Prepare
        val ioException = IOException("Error refreshing data")
        coEvery { api.getAllNominations() } throws ioException

        // Act
        repository.refreshData()

        // Assert
        coVerify { logger.error("IOException", match { it.contains("Error refreshing data") }) }
    }


    @Test
    fun `refresh data successfully updates database from API`() = runTest {
        // Arrange
        val mockNominations = listOf(Nomination("1", "1", "Test", "process", "2022-01-01", "2022-01-31"))
        val mockNominees = listOf(Nominee("1", "John", "Doe"))

        coEvery { api.getAllNominations() } returns DataWrapper(mockNominations)
        coEvery { api.getAllNominees() } returns DataWrapper(mockNominees)

        // Act
        repository.refreshData()

        // Assert
        coVerify { nominationDao.insertAllNominations(mockNominations) }
        coVerify { nominationDao.insertAllNominees(mockNominees) }
    }


    @Test
    fun `create nomination successfully creates nomination`() = runTest {
        // Arrange
        val nomination = Nomination("1", "1", "Reason", "Process", "2022-01-01", "2022-01-31")
        coEvery { api.createNomination("1", "Reason", "Process") } returns DataWrapper(nomination)

        // Act
        val result = repository.createNomination("1", "Reason", "Process")

        // Assert
        assertTrue(result is UiEvent.Success && result.data == nomination)
    }

    @Test
    fun `create nomination handles exceptions`() = runTest {
        // Arrange
        coEvery { api.createNomination(any(), any(), any()) } throws IOException("Network failure")

        // Act
        val result = repository.createNomination("1", "Reason", "Process")

        // Assert
        assertTrue(result is UiEvent.Error && result.message == "Network failure")
    }
}
