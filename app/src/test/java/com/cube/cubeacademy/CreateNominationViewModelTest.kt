package com.cube.cubeacademy

import app.cash.turbine.test
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.UiEvent
import com.cube.cubeacademy.viewmodel.CreateNominationViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CreateNominationViewModelTest {

    private lateinit var repository: Repository
    private lateinit var viewModel: CreateNominationViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = CreateNominationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `collectNominees emits loading then success`() = runTest {
        val nominees = listOf(
            Nominee("1", "First1", "Last1"),
            Nominee("2", "First2", "Last2")
        )
        coEvery { repository.getNominees() } returns flowOf(nominees)

        val job = launch {
            viewModel.events.test {
                viewModel.collectNominees()
                assert(awaitItem() is UiEvent.Loading)
                val successEvent = awaitItem() as UiEvent.Success
                assertEquals(nominees, successEvent.data)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()


    }

    @Test
    fun `collectNominees emits loading then error on failure`() = runTest {
        val errorMessage = "Error fetching data"
        coEvery { repository.getNominees() } returns flow {
            throw Exception(errorMessage)
        }
        val job = launch {
            viewModel.events.test {
                viewModel.collectNominees()
                assert(awaitItem() is UiEvent.Loading)
                val errorEvent = awaitItem()
                assert(errorEvent is UiEvent.Error)
                expectNoEvents()
            }
        }
        job.join() // Ensure the job completes


    }
}
