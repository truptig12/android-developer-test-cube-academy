package com.cube.cubeacademy

import app.cash.turbine.test
import com.cube.cubeacademy.lib.db.NominationWithNominee
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.utils.UiEvent
import com.cube.cubeacademy.viewmodel.NominationViewModel
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
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class NominationViewModelTest {

    private lateinit var repository: Repository
    private lateinit var viewModel: NominationViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = NominationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `collectNominations emits loading and then success on successful data fetch`() = runTest {
        val nomination = Nomination("1", "1", "reason", "process", "2023-10-11", "2023-11-11")
        val nominee = Nominee("1", "FirstTest1", "LastTest1")
        val nominations = listOf(NominationWithNominee(nomination, nominee))

        coEvery { repository.getNominationsWithNominees() } returns flowOf(nominations)

        val job = launch {
            viewModel.events.test {
                viewModel.collectNominations()
                assert(awaitItem() is UiEvent.Loading)
                assert(awaitItem() is UiEvent.Success<List<NominationWithNominee>>)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
    }

    @Test
    fun `collectNominations emits loading and then error on failure`() = runTest {
        val errorMessage = "Failed to fetch data"
        coEvery { repository.getNominationsWithNominees() } returns flow {
            throw Exception(errorMessage)
        }

        val job = launch {
            viewModel.events.test {
                viewModel.collectNominations()
                assert(awaitItem() is UiEvent.Loading)
                val uiError = awaitItem()
                assert(uiError is UiEvent.Error)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
    }
}

