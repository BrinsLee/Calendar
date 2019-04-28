package com.brins.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import org.w3c.dom.Text

class MainActivity : BaseActivity(),
CalendarView.OnCalendarSelectListener,
CalendarView.OnMonthChangeListener,
CalendarView.OnYearChangeListener,
CalendarView.OnWeekChangeListener,
        View.OnClickListener{

    lateinit var dialog : Dialog

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


    override fun onClick(v: View?) {
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
        val database = EventInfoDatabaseHelper.getInstance(this)
        val events = database.appDatabase.dao().getEvent()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(GroupItemDecoration<String,EventInfo>())
        var adapter = EventAdapter(this,events)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        val view = LinearLayout.inflate(this,R.layout.dialog_layout,null)
        view.findViewById<TextView>(R.id.date_start).text = "${calendarView.curMonth}月${calendarView.curDay}日上午10:00"
        view.findViewById<TextView>(R.id.date_stop).text = "${calendarView.curMonth}月${calendarView.curDay}日下午10:00"
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
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun initData() {
        var year = calendarView.curYear
        var month = calendarView.curMonth

        /*val map = HashMap<String, Calendar>()
        map[getSchemeCalendar(year, month, 3, -0xbf24db, "假").toString()] = getSchemeCalendar(year, month, 3, -0xbf24db, "假")
        map[getSchemeCalendar(year, month, 6, -0x196ec8, "事").toString()] = getSchemeCalendar(year, month, 6, -0x196ec8, "事")
        map[getSchemeCalendar(year, month, 9, -0x20ecaa, "议").toString()] = getSchemeCalendar(year, month, 9, -0x20ecaa, "议")
        map[getSchemeCalendar(year, month, 13, -0x123a93, "记").toString()] = getSchemeCalendar(year, month, 13, -0x123a93, "记")
        map[getSchemeCalendar(year, month, 14, -0x123a93, "记").toString()] = getSchemeCalendar(year, month, 14, -0x123a93, "记")
        map[getSchemeCalendar(year, month, 15, -0x5533bc, "假").toString()] = getSchemeCalendar(year, month, 15, -0x5533bc, "假")
        map[getSchemeCalendar(year, month, 18, -0x43ec10, "记").toString()] = getSchemeCalendar(year, month, 18, -0x43ec10, "记")
        map[getSchemeCalendar(year, month, 25, -0xec5310, "假").toString()] = getSchemeCalendar(year, month, 25, -0xec5310, "假")
        map[getSchemeCalendar(year, month, 27, -0xec5310, "多").toString()] = getSchemeCalendar(year, month, 27, -0xec5310, "多")
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        calendarView.setSchemeDate(map)*/

    }

    private fun getSchemeCalendar(year: Int, month: Int, day: Int, color: Int, text: String): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color//如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        calendar.addScheme(Calendar.Scheme())
        calendar.addScheme(-0xff7800, "假")
        calendar.addScheme(-0xff7800, "节")
        return calendar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)


    }
}
