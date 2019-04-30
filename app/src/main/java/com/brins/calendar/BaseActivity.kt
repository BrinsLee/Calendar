package com.brins.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.brins.calendar.database.EventInfoDatabaseHelper

abstract class BaseActivity : AppCompatActivity() {

    lateinit var database :EventInfoDatabaseHelper

    protected fun initWindow() {

    }

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = EventInfoDatabaseHelper.getInstance(this)
        initView()
        initData()
        initWindow()
    }

    @SuppressLint("InlineedApi")
    protected fun setStatusBarMode(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    }
}