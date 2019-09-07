package com.example.android.architecture.blueprints.todoapp.addedittask

import android.content.Context
import android.util.AttributeSet
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class AddEditTaskDatePicker : DatePicker {
    var textDate: Date? = Date()
        set(date) {
            field = date
            if (textDate == null)
                field = Date()
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = textDate

            updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))
        }


//    var textDate: String = ""
//        set(date) {
//            textDate = date
//            val calendar: Calendar = Calendar.getInstance()
//            calendar.time = SimpleDateFormat.getDateTimeInstance().parse(textDate)
//
//            updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))
//
//        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

}