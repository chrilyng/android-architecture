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
        }

    init {
        val currentTime = Date()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = currentTime
        val dateChangeListener = { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int  ->
            val updatedDate = GregorianCalendar(year, monthOfYear, dayOfMonth)
            textDate = updatedDate.time
        }
        init( currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE), dateChangeListener)
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