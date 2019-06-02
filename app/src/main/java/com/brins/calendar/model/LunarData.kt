package com.brins.calendar.model

import com.google.gson.annotations.SerializedName

class LunarData {
    var lunarYear : String = ""
    var lunarMonth : String = ""
    var lunarDay : String = ""
    @SerializedName("cnmonth")
    var monthTranditional : String = ""
    @SerializedName("cnday")
    var dayTranditional : String = ""

    var cyclicalYear : String = ""
    var cyclicalMonth : String = ""
    var cyclicalDay : String = ""
    var suit : String = ""
    var taboo : String = ""
    var animal : String = ""
    var week : String = ""
    var festivalList = arrayOf("")
}
