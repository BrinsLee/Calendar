package com.brins.calendar.utils

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.brins.calendar.R
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class DialogUtil private constructor(var context :Context,var view: View): DatePickerDialog.OnDateSetListener{
    @TargetApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    }

    val database = EventInfoDatabaseHelper.getInstance(context)
    val ed_title = view.findViewById<EditText>(R.id.ed_title)
    val tv_ahead = view.findViewById<TextView>(R.id.ahead)
    val ed_location = view.findViewById<EditText>(R.id.ed_location)
    val ed_event = view.findViewById<EditText>(R.id.ed_event)
    val ed_start = view.findViewById<TextView>(R.id.date_start)
    val ed_stop = view.findViewById<TextView>(R.id.date_stop)
    init {
        addListener()
    }


    companion object {
       fun Instance(context: Context, view: View): DialogUtil {
           return DialogUtil(context,view)
       }
    }

    fun addListener(){
        tv_ahead.setOnClickListener {
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                tv_ahead.text = "$hourOfDay:$minute"
            },0,0,true).show()
        }


    }
    fun createDialog():Dialog{
        val dialog = Dialog(context, R.style.BottomDialog)
        dialog.setContentView(view)
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.width = context.resources.displayMetrics.widthPixels - dp2px(16f)
        params.bottomMargin = dp2px(8f)
        view.layoutParams = params
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.window.setWindowAnimations(R.style.BottomDialog_Animation)
        return dialog
    }

    fun setStart(start : String){
        view.findViewById<TextView>(R.id.date_start).text = start

    }

    fun setEnd(end : String){
        view.findViewById<TextView>(R.id.date_stop).text = end
    }

    fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.resources.displayMetrics).toInt()
    }

    fun saveEvent():EventInfo?{

        var newEvent = EventInfo()
        var title = ed_title.text.toString()
        var location = ed_location.text.toString()
        var event = ed_event.text.toString()
        var start = ed_start.text.toString()
        var stop =  ed_stop.text.toString()
        when(getRandom()){
            1->newEvent.bg = R.drawable.bg_event
            2->newEvent.bg = R.drawable.bg_event_2
            3->newEvent.bg = R.drawable.bg_event_2

        }
        if (TextUtils.isEmpty(title)&&TextUtils.isEmpty(location)&&TextUtils.isEmpty(event)){
            return null
        }else{
            newEvent.affair = event
            newEvent.location = location
            if(!TextUtils.isEmpty(title))
                newEvent.title = title
            newEvent.start = "$start ${tv_ahead.text}:00"
            newEvent.stop = stop
            database.appDatabase.dao().addEvent(newEvent)
            setAlarm(newEvent)
        }
        return newEvent
    }

    private fun setAlarm(newEventInfo: EventInfo) {
        var intent = Intent("com.brins.calendar.MY_BROADCAST")
        Log.d("packagename","${context.packageName}")
        intent.setPackage(context.packageName)
        intent.putExtra("title",newEventInfo.title)
        intent.putExtra("dateStart","${newEventInfo.start}")
        intent.putExtra("dateStop","${newEventInfo.stop}")
        intent.putExtra("affair",newEventInfo.affair)
        intent.putExtra("location","${newEventInfo.location}")
//        context.sendBroadcast(intent)
        var time = newEventInfo.start
        var pendingIntent: PendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        var am : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP,coverToMillis(time),pendingIntent)


    }

    private fun getRandom(): Int {
        var i = Random().nextInt(2)+1
        return i

    }

    fun coverToMillis(time: String) : Long{
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd H:m:s").parse(time)
        Log.d("dialog","${calendar.timeInMillis}")
        return  calendar.timeInMillis
    }
}