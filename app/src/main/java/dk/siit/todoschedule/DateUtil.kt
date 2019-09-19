package dk.siit.todoschedule

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("textDate")
fun TextView.bindTextDate(date: Date?) {
    val updatedDate = date ?: Date()
    text = SimpleDateFormat.getDateInstance().format(updatedDate)
}