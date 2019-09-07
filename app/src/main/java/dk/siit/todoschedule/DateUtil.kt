package dk.siit.todoschedule

import android.widget.DatePicker
import androidx.databinding.BindingAdapter
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskDatePicker
import java.util.*

// TODO rewrite this for TextView if that makes sense
//@BindingAdapter("textDate")
//fun AddEditTaskDatePicker.bindTextDate(date: Date?) {
//    var updatedDate = date
//    if (updatedDate == null)
//        updatedDate = Date()
//    val calendar: Calendar = Calendar.getInstance()
//    calendar.time = updatedDate
//    updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))
//}