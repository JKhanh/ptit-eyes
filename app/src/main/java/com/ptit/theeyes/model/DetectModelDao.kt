package com.ptit.theeyes.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DetectModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveModel(detectModel: DetectModel)

    @Update
    fun updateModel(detectModel: DetectModel)

    @Delete
    fun deleteModel(detectModel: DetectModel)

    @Query("SELECT * FROM detectmodel")
    fun getAllModel(): LiveData<List<DetectModel>>
}