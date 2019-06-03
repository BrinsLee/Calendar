package com.brins.calendar

import android.annotation.SuppressLint
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.brins.calendar.model.EventInfo
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_event.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe



class EventActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private val handler = Handler()
    override fun initView() {

    }

    var whichbt = 0
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var eventInfo : EventInfo
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar1 = GregorianCalendar(year, monthOfYear, dayOfMonth)
        when (whichbt){
            1 -> {
                date_start.text = simpleDateFormat.format(calendar1.time)
            }
            0 -> date_stop.text = simpleDateFormat.format(calendar1.time)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    fun Event(messageEvent: EventInfo) {
        eventInfo = messageEvent
        init()
    }
    fun init() {

            val title = eventInfo.title
            val location = eventInfo.location
            val dateStart = eventInfo.start
            val dateStop = eventInfo.stop
            val affair = eventInfo.affair
            ed_title.setText(title)
            ed_location.setText(location)
            ed_event.setText(affair)
            date_start.text = dateStart
            date_stop.text = dateStop
            confirm.setOnClickListener {
                if (TextUtils.equals(title,ed_title.text)&&TextUtils.equals(location,ed_location.text)
                        &&TextUtils.equals(affair,ed_event.text)&&TextUtils.equals(dateStart,date_start.text)
                        &&TextUtils.equals(dateStop,date_stop.text)){
                    finish()
                }else {
                    loading_lay.visibility = View.VISIBLE
                    eventInfo.title = ed_title.text.toString()
                    eventInfo.location = ed_location.text.toString()
                    eventInfo.start = date_start.text.toString()
                    eventInfo.stop = date_stop.text.toString()
                    eventInfo.start = date_start.text.toString()
                    eventInfo.affair = ed_event.text.toString()
                    database.update(eventInfo)
                    handler.postDelayed({
                        setResult(1)
                        finish()
                    }, 1200)
                }

        }

        cancle.setOnClickListener {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        EventBus.getDefault().register(this)
        setSupportActionBar(toolbar_lay)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        return if (i == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}
