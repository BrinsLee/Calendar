package com.brins.calendar.broadcastreciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brins.calendar.EventActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent!!.setClass(context,EventActivity::class.java)
        intent.putExtra("alarm",true)
        context!!.startActivity(intent)
    }
}