package com.cube.cubeacademy

import app.cash.turbine.test
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.utils.UiEvent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryTest {
	@get:Rule
	var hiltRule = HiltAndroidRule(this)

	@Inject
	lateinit var repository: Repository

	@Before
	fun setUp() {
		hiltRule.inject()
	}

	@Test
	fun createNominationTest() = runTest {

		val response = repository.createNomination(nomineeId = "1", reason = "Good", process = "Process")
		assertTrue(response is UiEvent.Success)
		response as UiEvent.Success
	}

	@Test
	fun getNominationsTest()= runTest  {
		repository.getNominationsWithNominees().test {
			val event = awaitItem()
			Assert.assertTrue(event.isNotEmpty())
			cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun getNomineesTest() = runTest {
		repository.getNominees().test {
			val event = awaitItem()
			Assert.assertTrue(event.isNotEmpty())
			cancelAndIgnoreRemainingEvents()
		}
	}
}