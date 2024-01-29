package com.kiencute.basesrc.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import javax.annotation.Nullable

@Parcelize
@Entity(tableName = "beer")
data class Beer(
    @PrimaryKey
    val id: Int,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val description: String,
    val imageUrl: String,
    val abv: Double,
    val ibu: Float,
    val targetFg: Float,
    val targetOg: Int,
    val ebc: Int,
    val srm: Float,
    val ph: Double,
    val attenuationLevel: Float
) : Parcelable
