package com.ptit.theeyes.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity
@IgnoreExtraProperties
data class DetectModel(
    val description: String,
    val mid: String,
    val score: String
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
