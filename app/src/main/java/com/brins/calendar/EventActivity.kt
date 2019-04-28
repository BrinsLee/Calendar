package com.brins.calendar

import android.media.RingtoneManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.brins.calendar.database.EventInfoDatabaseHelper
import com.brins.calendar.model.EventInfo
import kotlinx.android.synthetic.main.activity_event.*

class EventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        setSupportActionBar(toolbar_lay)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val database = EventInfoDatabaseHelper.getInstance(this)
        var event = intent.getSerializableExtra("event") as EventInfo
        var alarm = intent.getBooleanExtra("alarm",false)
        ed_title.setText(event.title)
        ed_location.setText(event.location)
        ed_event.setText(event.affair)
        if (alarm){
            playmusic()
        }
    }

    private fun playmusic() {

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val r = RingtoneManager.getRingtone(this,notification)
        r.play()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        return if (i == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


}
