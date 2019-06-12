package com.brins.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    lateinit var view : View
    private var mYear: Int = 0
    private var mMonth = 0
    private var mDay : Int = 0
    private var handler = Handler()

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
        mMonth = calendar.month
        mDay = calendar.day
        dialogUtil.setStart("${calendar.year}-${calendar.month}-${calendar.day}")
        dialogUtil.setEnd("${calendar.year}-${calendar.month}-${calendar.day}")
        events = database.appDatabase.dao().getEventViaDate("${calendar.year}-${calendar.month}-${calendar.day}%")
        handler.postDelayed({
            getLunarData("$mYear-$mMonth-$mDay")
        },1200)
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
        mMonth = calendarView.curMonth
        mDay = calendarView.curDay
        tv_month_day.text = "${calendarView.curMonth}月${calendarView.curDay}日"
        tv_lunar.text = getString(R.string.today)
        today.text = "${calendarView.curDay}"
        events = database.appDatabase.dao().getEventViaDate("${calendarView.curYear}-${calendarView.curMonth}-${calendarView.curDay}%")
        adapter = EventAdapter(this,events)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(GroupItemDecoration<String,EventInfo>())
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        getLunarData("${calendarView.curYear}-${calendarView.curMonth}-${calendarView.curDay}")
        view = layoutInflater.inflate(R.layout.recyclerview_header,null,false)
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
            Toast.makeText(this,getString(R.string.saving),Toast.LENGTH_SHORT).show()
            handler.postDelayed({
                if (newevent != null){
                    dialog.dismiss()
                    events.add(newevent)
                    adapter.update(events)
                    Toast.makeText(this,getString(R.string.save_success),Toast.LENGTH_SHORT).show()
                }
            },1200)
        }
        adapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d("Adapter","$position")
                var intent = Intent(this@MainActivity,EventActivity::class.java)
                var event = events[position]
                EventBus.getDefault().postSticky(event)
                startActivityForResult(intent,1)
            }

        })
    }

    override fun onCreateAfterBinding() {
        super.onCreateAfterBinding()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(messageEvent : EventInfo) {}
    @SuppressLint("SetTextI18n")
    fun getLunarData(date : String){
        getLunarCalendar(Consumer{
            lunarData = it.data!!
            tv_lunar_year.text = lunarData.cyclicalYear
            tv_lunar_animal.text = "${lunarData.animal}年"
            view.findViewById<TextView>(R.id.date_num).text = lunarData.lunarDay
            view.findViewById<TextView>(R.id.date_lunar).text = "${lunarData.monthTranditional}月${lunarData.dayTranditional}"
            view.findViewById<TextView>(R.id.suit).text = lunarData.suit
            view.findViewById<TextView>(R.id.taboo).text = lunarData.taboo
            adapter.addHeaderView(view)

        },Consumer{},date)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1){
            events = database.appDatabase.dao().getEventViaDate("$mYear-$mMonth-$mDay%")
            adapter.update(events)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }


    inner class EventAdapter (val context: Context, var events : MutableList<EventInfo>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private var mHeaderViews : SparseArray<View> = SparseArray()
        private var itemClickListener: OnItemClickListener? = null
        private var BASE_ITEM_TYPE_HEADER = 10000000
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
            if (isHeaderPosition(i)){
                return
            }
            val position = i - mHeaderViews.size()
            var vh : EventAdapterViewHolder= holder as EventAdapterViewHolder
            holder.title.text = events[position].title
            holder.date.text = "${events[position].start}"
            holder.affair.text = events[position].affair
            if (events[position].bg == null){
                events[position].bg = R.drawable.bg_event
                database.update(events[position])
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.layout.background = context.resources.getDrawable(events[position].bg!!,null)
            }else{
                holder.layout.background = context.resources.getDrawable(events[position].bg!!)
            }
            holder.layout.setOnClickListener { this.itemClickListener!!.onItemClick(position) }
            holder.del.setOnClickListener{
                removeData(position)
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (isHeaderPosition(position)) {
                // 直接返回position位置的key
                return mHeaderViews.keyAt(position)
            }
            return super.getItemViewType(position - mHeaderViews.size())

        }
        private fun isHeaderPosition(position: Int): Boolean {
            return position < mHeaderViews.size()
        }


        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            if (isHeaderViewType(p1)) {
                var headerView = mHeaderViews.get(p1)
                return HeadItemViewHolder(headerView)
            }
            return EventAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleritem, parent, false))
        }

        private fun isHeaderViewType(viewType : Int):Boolean {
            var position = mHeaderViews.indexOfKey(viewType)
            return position >= 0
        }

        override fun getItemCount(): Int {
            return events.size + mHeaderViews.size()
        }

        fun  addHeaderView(view : View) {
            val position = mHeaderViews.indexOfValue(view)
            if (position < 0) {
                mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view)
            }
            notifyDataSetChanged()
        }

        fun removeHeaderView(view : View) {
            val index = mHeaderViews.indexOfValue(view)
            if (index < 0) return
            mHeaderViews.removeAt(index)
            notifyDataSetChanged()
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

     class HeadItemViewHolder (itemView : View): RecyclerView.ViewHolder(itemView) {
         var dateNum : TextView = itemView.findViewById(R.id.date_num)
         var dateLunar : TextView = itemView.findViewById(R.id.date_lunar)
         var suit : TextView = itemView.findViewById(R.id.suit)
         var taboo : TextView = itemView.findViewById(R.id.taboo)
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
