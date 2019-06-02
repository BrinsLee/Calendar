package com.brins.calendar.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.brins.calendar.model.EventInfo
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import android.support.annotation.VisibleForTesting



@Database(entities = arrayOf(EventInfo::class),version = 3,exportSchema = false)

abstract class EventInfoDatabase : RoomDatabase() {
    abstract fun dao():Dao

    companion object{
        @VisibleForTesting
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE event ADD COLUMN background INTEGER")
            }
        }
    }
}