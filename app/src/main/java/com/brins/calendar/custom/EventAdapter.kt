package com.brins.calendar.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.brins.calendar.EventActivity
import com.brins.calendar.R
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import java.util.*

class EventAdapter (val context: Context,var events : MutableList<EventInfo>,var database : EventInfoDatabaseHelper): RecyclerView.Adapter<EventAdapter.Companion.EventAdapterViewHolder>() {


    private var itemClickListener: OnItemClickListener? = null
    override fun onBindViewHolder(holder: EventAdapterViewHolder, i: Int) {

        holder.title.text = events[i].title
        holder.date.text = "${events[i].start}-${events[i].stop}"
        holder.layout.setOnClickListener { this.itemClickListener!!.onItemClick(i) }
        holder.del.setOnClickListener{
            removeData(i)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EventAdapterViewHolder {
        return EventAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleritem,parent,false))
    }

    override fun getItemCount(): Int {
        return events.size
    }


    companion object {
        class EventAdapterViewHolder (itemView : View): RecyclerView.ViewHolder(itemView){
            var title : TextView = itemView.findViewById(R.id.tv_title) as TextView
            var date : TextView = itemView.findViewById(R.id.tv_date) as TextView
            var layout = itemView.findViewById(R.id.item_layout) as LinearLayout
            var del = itemView.findViewById(R.id.del) as ImageView
        }

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }
    }

    fun addData(position: Int, data: EventInfo) {
        events.add(position, data)
        notifyItemInserted(position)
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
}