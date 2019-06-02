package com.brins.calendar.api

import com.brins.calendar.api.AppConfig.LUNARCALENDAR
import com.brins.calendar.api.AppConfig.USER_AGENT
import com.brins.calendar.model.CalendarResult
import com.brins.calendar.model.LunarData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LunarCalendarApi {

    @Headers("Accept: */*",
            "User-Agent: $USER_AGENT")
    @GET(LUNARCALENDAR)
    fun getCalendar(@Query("date")date : String): Observable<CalendarResult>
}