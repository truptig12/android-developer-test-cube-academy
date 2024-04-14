package com.cube.cubeacademy.lib.db

import androidx.room.Embedded
import androidx.room.Relation
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee


/**
 * A data class that represents a relation between Nomination and Nominee entities.
 * This class is used to fetch data from both Nomination and Nominee tables with a single query,
 * linking them through the "nomineeId", which acts as a foreign key.
 *
 * @Embedded This annotation is used here to indicate that the Nomination object's fields can be considered
 * directly part of the containing data table.
 * @Relation It is used to associate the 'nomineeId' from Nomination as a foreign key to 'nomineeId' in the Nominee entity.
 */
data class NominationWithNominee(
    @Embedded val nomination: Nomination,
    @Relation(
        parentColumn = "nomineeId",
        entityColumn = "nomineeId"
    )
    val nominee: Nominee
)
