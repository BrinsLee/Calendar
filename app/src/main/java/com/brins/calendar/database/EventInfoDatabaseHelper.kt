package com.brins.calendar.database

import android.arch.persistence.room.Room
import android.content.Context
import com.brins.calendar.model.EventInfo

class EventInfoDatabaseHelper(context :Context) {

    val appDatabase = Room.databaseBuilder(context,EventInfoDatabase::class.java,"event")
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
}