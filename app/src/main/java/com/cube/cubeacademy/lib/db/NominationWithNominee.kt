package com.cube.cubeacademy.lib.db

import androidx.room.Embedded
import androidx.room.Relation
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee

data class NominationWithNominee(
    @Embedded val nomination: Nomination,
    @Relation(
        parentColumn = "nomineeId",
        entityColumn = "nomineeId"
    )
    val nominee: Nominee
)
