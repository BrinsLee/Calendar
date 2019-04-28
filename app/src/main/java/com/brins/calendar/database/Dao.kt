package com.brins.calendar.database

import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.brins.calendar.model.EventInfo

@android.arch.persistence.room.Dao
interface Dao {
    @Insert
    fun addEvent(collection : EventInfo)

    @Query("select * from event order by ID DESC")
    fun getEvent():MutableList<EventInfo>

    @Query("select * from event where ID = :id")
    fun getEventViaId(id : Int):MutableList<EventInfo>

    @Query("select * from event where date_start like :date order by ID DESC")
    fun getEventViaDate(date : String): List<EventInfo>

    @Query("delete from event where date_start =:date")
    fun deleteViaDate(date : String)
}