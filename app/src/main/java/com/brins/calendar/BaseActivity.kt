package com.brins.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.brins.calendar.database.EventInfoDatabaseHelper
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {

    open lateinit var database :EventInfoDatabaseHelper
    protected abstract fun initView()
    open fun onCreateAfterBinding(){
    }
    open fun onCreateBeforeBinding(){
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateBeforeBinding()
        database = EventInfoDatabaseHelper.getInstance(this)
        initView()
    }

    @SuppressLint("InlineedApi")
    protected fun setStatusBarMode(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    }

}