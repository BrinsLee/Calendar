package com.brins.calendar.database

import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.brins.calendar.model.EventInfo

@android.arch.persistence.room.Dao
interface Dao {
    @Insert
    fun addEvent(collection : EventInfo)

    @Query("select * from event order by ID ASC")
    fun getEvent():MutableList<EventInfo>

    @Query("select * from event where ID = :id")
    fun getEventViaId(id : Int):MutableList<EventInfo>

    @Query("select * from event where date_start like :date order by ID DESC")
    fun getEventViaDate(date : String): MutableList<EventInfo>

    @Query("delete from event where date_start =:date")
    fun deleteViaDate(date : String)

    @Query("delete from event where ID =:id")
    fun deleteViaId(id : Int)

    @Query("update event set title = :title,date_start = :start,date_stop = :stop,affair = :affair,location = :location where ID = :mid")
    fun updateViaId(title:String , start:String ,stop:String,affair:String,location: String ,mid:Int)
}