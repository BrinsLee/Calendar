package com.brins.calendar.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.brins.calendar.EventActivity
import com.brins.calendar.R
import com.brins.calendar.model.EventInfo

class EventAdapter (val context: Context,var events : MutableList<EventInfo>): RecyclerView.Adapter<EventAdapter.Companion.EventAdapterViewHolder>() {


    private var itemClickListener: OnItemClickListener? = null
    override fun onBindViewHolder(holder: EventAdapterViewHolder, i: Int) {

        holder.title.text = events[i]?.title
        holder.date.text = "${events[i]?.start}-${events[i]?.stop}"
        holder.layout.setOnClickListener{
            var intent = Intent(context,EventActivity::class.java)
            intent.putExtra("event",events[i])
            context.startActivity(intent)
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
        }

        interface OnItemClickListener {
            fun onItemClick(view: View)
        }
    }

    fun addData(position: Int, data: EventInfo) {
        events.add(position, data)
        notifyItemInserted(position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }
}