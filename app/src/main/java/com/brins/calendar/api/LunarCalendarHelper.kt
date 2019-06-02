package com.brins.calendar.api

import android.annotation.SuppressLint
import com.brins.calendar.api.AppConfig.BASE_URL
import com.brins.calendar.model.CalendarResult
import com.brins.calendar.model.LunarData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LunarCalendarHelper {
    fun getRetrofitFactory(baseurl : String = BASE_URL) : LunarCalendarApi{
        val retrofit = Retrofit.Builder().baseUrl(baseurl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build()
        return retrofit.create(LunarCalendarApi::class.java)
    }

    private fun getClient(): OkHttpClient {
        val builder : OkHttpClient.Builder = OkHttpClient().newBuilder()
        val client = builder.connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
        return client
    }

    @SuppressLint("CheckResult")
    fun getLunarCalendar(consumer: Consumer<CalendarResult>, error : Consumer<Throwable>, date : String){
        getRetrofitFactory().getCalendar(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer,error)

    }
}