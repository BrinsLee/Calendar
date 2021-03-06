package com.brins.calendar.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import java.io.Serializable

@Entity(tableName = "event")
class EventInfo : Serializable {
    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var mId: Int = 0

    @ColumnInfo(name = "title")
    var title : String = "无标题"
    @ColumnInfo(name = "date_start")
    var start : String = ""
    @ColumnInfo(name = "date_stop")
    var stop : String = ""
    @ColumnInfo(name = "ahead")
    var ahead : String = ""
    @ColumnInfo(name = "affair")
    var affair : String = ""
    @ColumnInfo(name = "location")
    var location : String = ""
    @ColumnInfo(name = "background")
    @DrawableRes
    var bg : Int? = null
}