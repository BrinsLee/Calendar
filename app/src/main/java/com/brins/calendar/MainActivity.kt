package com.brins.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.brins.calendar.api.LunarCalendarHelper.getLunarCalendar
import com.brins.calendar.custom.GroupItemDecoration
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import com.brins.calendar.model.LunarData
import com.brins.calendar.utils.DialogUtil
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(),
CalendarView.OnCalendarSelectListener,
CalendarView.OnMonthChangeListener,
CalendarView.OnYearChangeListener,
CalendarView.OnWeekChangeListener{

    lateinit var dialog : Dialog
    lateinit var events : MutableList<EventInfo>
    lateinit var adapter : EventAdapter
    var selectYear  = ""
    var selectMonth  = ""
    var selectDay = ""
    lateinit var dialogStart : TextView
    lateinit var dialogEnd : TextView
    lateinit var dialogUtil : DialogUtil
    lateinit var lunarData : LunarData
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

    @SuppressLint("RestrictedApi")
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        tv_lunar.visibility = View.VISIBLE
        tv_year.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
        layout_lunar.visibility = View.VISIBLE
        tv_month_day.text = "${calendar!!.month}月${calendar.day}日"
        tv_year.text = "${calendar.year}"
        tv_lunar.text = "${calendar.lunar}"
        mYear = calendar.year
        dialogUtil.setStart("${calendar.year}-${calendar.month}-${calendar.day}")
        dialogUtil.setEnd("${calendar.year}-${calendar.month}-${calendar.day}")
        events = database.appDatabase.dao().getEventViaDate("${calendar.year}-${calendar.month}-${calendar.day}")
        if (events.size == 0) {
            Toast.makeText(this,"无事件",Toast.LENGTH_SHORT).show()
        }
        adapter.update(events)
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
            layout_lunar.visibility = View.GONE
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
        events = database.appDatabase.dao().getEventViaDate("${calendarView.curYear}-${calendarView.curMonth}-${calendarView.curDay}")
        adapter = EventAdapter(this,events)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(GroupItemDecoration<String,EventInfo>())
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        getLunarData("${calendarView.curYear}-${calendarView.curMonth}-${calendarView.curDay}")
        val view = LinearLayout.inflate(this,R.layout.dialog_layout,null)
        dialogUtil = DialogUtil.Instance(this,view)
        dialogUtil.setStart("$mYear-${calendarView.curMonth}-${calendarView.curDay}")
        dialogUtil.setEnd("$mYear-${calendarView.curMonth}-${calendarView.curDay}")
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
                adapter.addData(newevent)
                events = database.appDatabase.dao().getEvent()
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show()
            }
        }
        adapter.setOnItemClickListener(object : OnItemClickListener{
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

    @SuppressLint("SetTextI18n")
    fun getLunarData(date : String){
        getLunarCalendar(Consumer{
            lunarData = it.data!!
            tv_lunar_year.text = lunarData.cyclicalYear
            tv_lunar_animal.text = "${lunarData.animal}年"
        },Consumer{},date)
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


    inner class EventAdapter (val context: Context, var events : MutableList<EventInfo>): RecyclerView.Adapter<EventAdapterViewHolder>() {


        private var itemClickListener: OnItemClickListener? = null
        override fun onBindViewHolder(holder: EventAdapterViewHolder, i: Int) {
            holder.title.text = events[i].title
            holder.date.text = "${events[i].start}"
            holder.affair.text = events[i].affair
            if (events[i].bg == null){
                events[i].bg = R.mipmap.bg_event
                database.update(events[i])
            }
            holder.layout.background = context.resources.getDrawable(events[i].bg!!,null)
            holder.layout.setOnClickListener { this.itemClickListener!!.onItemClick(i) }
            holder.del.setOnClickListener{
                removeData(i)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EventAdapterViewHolder {
            return EventAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleritem, parent, false))
        }

        override fun getItemCount(): Int {
            return events.size
        }


        fun addData(data: EventInfo) {
            events.add(events.size, data)
            notifyItemInserted(events.size-1)
        }

        fun removeData(position: Int){
            if (events.size == 0) {
                return
            }
            database.appDatabase.dao().deleteViaId(events[position].mId)
            events.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.itemClickListener = listener
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun remove(update:MutableList<EventInfo>){
            events.clear()
            events = update
            notifyDataSetChanged()
        }

        fun update(events: MutableList<EventInfo>){
            this.events = events
            notifyDataSetChanged()
        }
    }

    class EventAdapterViewHolder (itemView : View): RecyclerView.ViewHolder(itemView){
        var title : TextView = itemView.findViewById(R.id.tv_title)
        var date : TextView = itemView.findViewById(R.id.tv_date)
        var layout = itemView.findViewById(R.id.item_layout) as ConstraintLayout
        var affair : TextView = itemView.findViewById(R.id.tv_affair)
        var del = itemView.findViewById(R.id.del) as ImageView
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}
