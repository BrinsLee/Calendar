package com.brins.calendar.utils

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.util.GregorianCalendar
import android.os.Build
import android.text.TextUtils
import android.util.EventLog
import android.view.ViewGroup
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.brins.calendar.EventActivity
import com.brins.calendar.R
import com.brins.calendar.broadcastreciver.AlarmReceiver
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.wx.wheelview.widget.WheelView
import org.w3c.dom.Text
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.text.SimpleDateFormat
import java.util.*


class DialogUtil private constructor(var context :Context,var view: View): DatePickerDialog.OnDateSetListener{
    @TargetApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val calendar1 = GregorianCalendar(year, monthOfYear, dayOfMonth)
        when (whichbt){
            1 -> {
                ed_start.text = simpleDateFormat.format(calendar1.time)
            }
            0 -> ed_stop.text = simpleDateFormat.format(calendar1.time)
        }
    }

    val database = EventInfoDatabaseHelper.getInstance(context)
    var whichbt = 0
    val ed_title = view.findViewById<EditText>(R.id.ed_title)
    val tv_ahead = view.findViewById<TextView>(R.id.ahead)
    val ed_location = view.findViewById<EditText>(R.id.ed_location)
    val ed_event = view.findViewById<EditText>(R.id.ed_event)
    val ed_start = view.findViewById<TextView>(R.id.date_start)
    val ed_stop = view.findViewById<TextView>(R.id.date_stop)
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    init {
        addListener()
    }


    companion object {
       fun Instance(context: Context, view: View): DialogUtil {
           return DialogUtil(context,view)
       }
    }

    fun addListener(){
        var date_dialog=SpinnerDatePickerDialogBuilder()
                .context(context)
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(2019, 3, 27)
                .maxDate(2030, 0, 1)
                .minDate(2018, 0, 1)
                .build()

        ed_start.setOnClickListener {
            date_dialog.show()
            whichbt = 1
        }
        ed_stop.setOnClickListener {
            date_dialog.show()
            whichbt = 0
        }
        tv_ahead.setOnClickListener {
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
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
        if (TextUtils.isEmpty(title)&&TextUtils.isEmpty(location)&&TextUtils.isEmpty(event)){
            return null
        }else{
            newEvent.affair = event
            newEvent.location = location
            if(!TextUtils.isEmpty(title))
                newEvent.title = title
            newEvent.start = start
            newEvent.stop = stop
            database.appDatabase.dao().addEvent(newEvent)
            setAlarm(newEvent)
        }
        return newEvent
    }

    private fun setAlarm(newEvent: EventInfo) {
        var intent = Intent("com.brins.calendar.action")
        var time = "${ed_start.text} ${tv_ahead.text}:0"
        intent.putExtra("event",newEvent)
        var pendingIntent: PendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT)
        var am : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExact(AlarmManager.RTC_WAKEUP,coverToMillis(time),pendingIntent)


    }
    fun coverToMillis(time: String) : Long{
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd H:m:s").parse(time)
        return  calendar.timeInMillis
    }

}