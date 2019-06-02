package com.brins.calendar.database

import android.arch.persistence.room.Room
import android.content.Context
import com.brins.calendar.model.EventInfo

class EventInfoDatabaseHelper(context :Context) {

    val appDatabase = Room.databaseBuilder(context,EventInfoDatabase::class.java,"event")
            .addMigrations(EventInfoDatabase.MIGRATION_2_3)
            .allowMainThreadQueries().build()

    companion object {
        @Volatile
        var INSTANCE: EventInfoDatabaseHelper? = null

        fun getInstance(context: Context): EventInfoDatabaseHelper {
            if (INSTANCE == null) {
                synchronized(EventInfoDatabaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = EventInfoDatabaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }
    }
    fun update(event: EventInfo) {
        appDatabase.dao().updateViaId(event.title,event.start,event.stop,event.affair,event.location,event.mId)
    }
}