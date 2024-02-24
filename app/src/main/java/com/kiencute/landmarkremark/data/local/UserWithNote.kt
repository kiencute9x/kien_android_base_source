package com.kiencute.landmarkremark.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.entities.User

data class UserWithNotes(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val notes: List<Note>
)
