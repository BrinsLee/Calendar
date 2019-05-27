package com.brins.calendar

import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_event.*
import java.text.SimpleDateFormat

class EventActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    var whichbt = 0
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

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
    override fun initView() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        setSupportActionBar(toolbar_lay)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.getBooleanExtra("broad",false)){

            val title = intent.getStringExtra("title")
            val location = intent.getStringExtra("location")
            val dateStart = intent.getStringExtra("dateStart")
            val dateStop = intent.getStringExtra("dateStop")
            val affair = intent.getStringExtra("affair")
            ed_title.setText(title)
            ed_location.setText(location)
            ed_event.setText(affair)
            date_start.text = dateStart
            date_stop.text = dateStop
            confirm.visibility = View.GONE
        }else{
            var i = intent.getIntExtra("event",0)
            var events = database.appDatabase.dao().getEventViaId(i)
            Log.d("EventAct","${events.size}")
            var event = events[0]
            val title = event.title
            val location = event.location
            val affair = event.affair
            val dateStart = event.start
            val dateStop = event.stop
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
                }
                event.title = ed_title.text.toString()
                event.location = ed_location.text.toString()
                event.start = date_start.text.toString()
                event.stop = date_stop.text.toString()
                event.start = date_start.text.toString()
                event.affair = ed_event.text.toString()
                database.update(event)
                setResult(1)
                finish()

            }
            addListener()
        }

        cancle.setOnClickListener {
            finish()
        }


    }
    fun addListener(){
        var date_dialog= SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2019, 3, 27)
                .maxDate(2030, 0, 1)
                .minDate(2018, 0, 1)
                .build()

        date_start.setOnClickListener {
            date_dialog.show()
            whichbt = 1
        }
        date_stop.setOnClickListener {
            date_dialog.show()
            whichbt = 0
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
