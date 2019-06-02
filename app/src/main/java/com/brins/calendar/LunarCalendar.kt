package com.brins.calendar

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class LunarCalendar : BaseActivity() {

    companion object{
        fun startThisActivity(activity: Activity){
            val intent = Intent(activity,LunarCalendar::class.java)
            activity.startActivity(intent)
        }
    }
    override fun initView() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunar_calendar)
    }
}
