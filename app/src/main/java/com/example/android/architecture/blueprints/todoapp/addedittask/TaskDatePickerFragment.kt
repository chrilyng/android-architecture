package com.example.android.architecture.blueprints.todoapp.addedittask

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TaskDatePickerFragment constructor(viewModelParameter: AddEditTaskViewModel) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val viewModel = viewModelParameter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default values for the picker
        val c = Calendar.getInstance()
        val year = viewModel.remindYear.value ?: c.get(Calendar.YEAR)
        val month = viewModel.remindMonth.value ?: c.get(Calendar.MONTH)
        val day = viewModel.remindDay.value ?: c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(context, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.remindYear.value = year
        viewModel.remindMonth.value = month
        viewModel.remindDay.value = dayOfMonth
    }
}