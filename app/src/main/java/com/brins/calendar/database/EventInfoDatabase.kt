package com.brins.calendar.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.brins.calendar.model.EventInfo

@Database(entities = arrayOf(EventInfo::class),version = 2,exportSchema = false)

abstract class EventInfoDatabase : RoomDatabase() {
    abstract fun dao():Dao
}