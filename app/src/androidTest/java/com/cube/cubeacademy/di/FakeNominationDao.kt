package com.cube.cubeacademy.di

import com.cube.cubeacademy.lib.db.NominationDao
import com.cube.cubeacademy.lib.db.NominationWithNominee
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FakeNominationDao : NominationDao {
    private val nominees = mutableListOf(
        Nominee("1", "FirstTest1", "LastTest1"),
        Nominee("2", "FirstTest2", "LastTest2"),
        Nominee("3", "FirstTest3", "LastTest3"),
    )
    private val nominations = mutableListOf(
        Nomination("1", "1", "reason", "process", "2023-10-11", "2023-11-11"),
        Nomination("2", "2", "reason", "process", "2023-10-11", "2023-11-11"),
        Nomination("1", "3", "reason", "process", "2023-10-11", "2023-11-11"),
        Nomination("2", "4", "reason", "process", "2023-10-11", "2023-11-11"),
    )
    private val mutex = Mutex()

    override suspend fun insertAllNominees(nominee: List<Nominee>) {
        mutex.withLock {
            this.nominees.addAll(nominee)
        }
    }

    override fun getAllNominees(): Flow<List<Nominee>> = flow {
        emit(mutex.withLock { nominees.toList() }) // Emit a snapshot of the current list
    }

    override suspend fun insertAllNominations(nomination: List<Nomination>) {
        mutex.withLock {
            nominations.addAll(nomination)
        }
    }

    override fun getNominationsWithNominees(): Flow<List<NominationWithNominee>> = flow {
        val nominationWithNominees = mutex.withLock {
            nominations.map { nomination ->
                NominationWithNominee(
                    nomination,
                    nominees.find { it.nomineeId == nomination.nomineeId } ?: Nominee(
                        "0",
                        "Unknown",
                        "Unknown"
                    )
                )
            }
        }
        emit(nominationWithNominees)
    }
}

