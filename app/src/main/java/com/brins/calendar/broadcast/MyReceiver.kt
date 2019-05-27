package com.brins.calendar.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.brins.calendar.R
import android.app.Notification
import android.app.NotificationChannel
import android.graphics.Color
import android.media.RingtoneManager
import com.brins.calendar.EventActivity
import android.app.PendingIntent

class MyReceiver : BroadcastReceiver() {

    val id = "my_chancel"
    override fun onReceive(context: Context, intent: Intent) {

        val manager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notify : Notification
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val title = intent.getStringExtra("title")
        val location = intent.getStringExtra("location")
        val dateStart = intent.getStringExtra("dateStart")
        val dateStop = intent.getStringExtra("dateStop")
        val affair = intent.getStringExtra("affair")
        intent.setClass(context,EventActivity::class.java)
        intent.putExtra("broad",true)
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val mChannel = NotificationChannel(id, "提醒", NotificationManager.IMPORTANCE_HIGH)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)
            mChannel.enableLights(true)
            mChannel.lightColor = Color.GREEN
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE;
            mChannel.setSound(sound,null)
            manager.createNotificationChannel(mChannel)
            notify = NotificationCompat.Builder(context,"notify")
                    .setChannelId(id)
                    .setSmallIcon(R.drawable.ic_date)
                    .setTicker("$title")
                    .setContentTitle("$dateStart - $dateStop")
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText("$affair 地点:$location"))
                    .setNumber(1).build()
        }else {
            notify = NotificationCompat.Builder(context, "notify")
                    .setSmallIcon(R.drawable.ic_date)
                    .setTicker("$title")
                    .setContentTitle("$dateStart - $dateStop")
                    .setLights(Color.GREEN,1,1)
                    .setSound(sound)
                    .setVibrate(longArrayOf(100, 200, 100, 200))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle().bigText("${affair}地点:$location"))
                    .setNumber(1).build()
        }
            manager.notify(1000,notify)
        }
}
