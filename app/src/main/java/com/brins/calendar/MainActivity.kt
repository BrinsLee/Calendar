package com.brins.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.brins.calendar.custom.EventAdapter
import com.brins.calendar.custom.GroupItemDecoration
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import com.brins.calendar.utils.DialogUtil
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
CalendarView.OnCalendarSelectListener,
CalendarView.OnMonthChangeListener,
CalendarView.OnYearChangeListener,
CalendarView.OnWeekChangeListener{

    lateinit var dialog : Dialog
    lateinit var events : MutableList<EventInfo>
    lateinit var adapter : EventAdapter

    override fun onWeekChange(weekCalendars: MutableList<Calendar>?) {

    }

    @SuppressLint("SetTextI18n")
    override fun onMonthChange(year: Int, month: Int) {

        var calendar = calendarView.selectedCalendar
        tv_lunar.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        tv_month_day.text = "${calendar.month}月${calendar.day}日"
        tv_year.text = "${calendar.year}"
        tv_lunar.text = calendar.lunar
        mYear = calendar.year
    }

    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {

        tv_lunar.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
        tv_month_day.text = "${calendar!!.month}月${calendar.day}日"
        tv_year.text = "${calendar.year}"
        tv_lunar.text = "${calendar.lunar}"
        mYear = calendar.year
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show()

    }

    override fun onYearChange(year: Int) {
        tv_month_day.text = "$year"
    }

    private var mYear: Int = 0

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun initView() {
        setStatusBarMode()
        tv_month_day.setOnClickListener {
            if (!calendarLayout.isExpand){
                calendarLayout.expand()
                return@setOnClickListener
            }
            calendarView.showYearSelectLayout(mYear)
            fab.visibility = View.GONE
            tv_lunar.visibility = View.GONE
            tv_year.visibility = View.GONE
            tv_month_day.text = ("$mYear")
        }
        current.setOnClickListener {
            calendarView.scrollToCurrent()
        }
        fab.setOnClickListener {
            dialog.show()
        }
        calendarView.setOnYearChangeListener(this)
        calendarView.setOnMonthChangeListener(this)
        calendarView.setOnCalendarSelectListener(this)
        tv_year.text = "${calendarView.curYear}"
        mYear = calendarView.curYear
        tv_month_day.text = "${calendarView.curMonth}月${calendarView.curDay}日"
        tv_lunar.text = "今日"
        today.text = "${calendarView.curDay}"
        var database = EventInfoDatabaseHelper.getInstance(this)
        events = database.appDatabase.dao().getEvent()
        adapter = EventAdapter(this,events,database)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(GroupItemDecoration<String,EventInfo>())
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        val view = LinearLayout.inflate(this,R.layout.dialog_layout,null)
        view.findViewById<TextView>(R.id.date_start).text = "${mYear}-${calendarView.curMonth}-${calendarView.curDay}"
        view.findViewById<TextView>(R.id.date_stop).text = "${mYear}-${calendarView.curMonth}-${calendarView.curDay}"
        val dialogUtil = DialogUtil.Instance(this,view)
        dialog = dialogUtil.createDialog()
        val cancle = view.findViewById<TextView>(R.id.cancle)
        val confirm = view.findViewById<TextView>(R.id.confirm)
        cancle.setOnClickListener {
            dialog.dismiss()
        }
        confirm.setOnClickListener {
            var newevent = dialogUtil.saveEvent()
            if (newevent != null){
                dialog.dismiss()
                adapter.addData(events.size,newevent)
                events = database.appDatabase.dao().getEvent()
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
            }
        }
        adapter.setOnItemClickListener(object : EventAdapter.Companion.OnItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d("Adapter","$position")
                var intent = Intent(this@MainActivity,EventActivity::class.java)
                var event = events[position]
                Log.d("Adapter","${event.mId}")
                intent.putExtra("event",event.mId)
                startActivityForResult(intent,1)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1){
            events = database.appDatabase.dao().getEvent()
            adapter.remove(events)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
    }


}
