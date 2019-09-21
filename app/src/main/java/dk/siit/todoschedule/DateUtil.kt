package dk.siit.todoschedule

import android.text.format.DateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

@BindingAdapter("textDate")
fun TextView.bindTextDate(date: Date?) {
    text = context.getString(R.string.pick_remind_date)
    date?.let {
        val dateTimeFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEE, MMM d, yyyy")
        text = DateFormat.format(dateTimeFormat, date)
    }
}

@BindingAdapter("textTime")
fun TextView.bindTextTime(date: Date?) {
    text = context.getString(R.string.pick_remind_time)
    date?.let {
        val dateTimeFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), "jmm")
        text = DateFormat.format(dateTimeFormat, date)
    }
}

@BindingAdapter("textDateTime")
fun TextView.bindTextDateTime(date: Date?) {
    text = context.getString(R.string.empty_date)
    date?.let {
        val dateTimeFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), "jmm MMM d, yyyy")
        text = DateFormat.format(dateTimeFormat, date)
    }
}