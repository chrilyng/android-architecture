package com.example.android.architecture.blueprints.todoapp.addedittask

import android.content.Context
import android.util.AttributeSet
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class AddEditTaskDatePicker : DatePicker {

    // TODO fix that it starts in January?

    // this was mostly messing with bindings?
//    var textDate: Date? = Date()
//        get() {
//            if (field == null)
//                return Date()
//            return field
//        }
//        set(date) {
//            field = date
//            if (textDate == null)
//                field = Date()
//            val calendar: Calendar = Calendar.getInstance()
//            calendar.time = textDate
//        }
//
//    init {
//        val currentTime = Date()
//        val currentCalendar = Calendar.getInstance()
//        currentCalendar.time = currentTime
//        val dateChangeListener = { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int  ->
//            val updatedDate = GregorianCalendar(year, monthOfYear, dayOfMonth)
//            textDate = updatedDate.time
//        }
//        init( currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DATE), dateChangeListener)
//    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

}