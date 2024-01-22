package com.olegstashkiv.umbrella.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["location_name"], unique = true)])
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "location_name") val locationName: String
)