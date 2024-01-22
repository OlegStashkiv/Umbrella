package com.olegstashkiv.umbrella.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.olegstashkiv.umbrella.data.entity.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Query("SELECT * FROM location ORDER BY location_name")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM location WHERE location_name = :locationName")
    fun get(locationName: String): Flow<Location>
}