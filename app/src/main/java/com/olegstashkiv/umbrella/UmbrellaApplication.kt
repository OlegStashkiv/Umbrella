package com.olegstashkiv.umbrella

import android.app.Application
import com.olegstashkiv.umbrella.data.db.LocationRoomDatabase

class UmbrellaApplication : Application() {
    val database: LocationRoomDatabase by lazy { LocationRoomDatabase.getDatabase(this) }
}