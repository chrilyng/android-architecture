/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.addedittask

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import dk.siit.todoschedule.R
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTaskViewModel(
        private val tasksRepository: TasksRepository
) : ViewModel() {

    // Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    val remindDate = MediatorLiveData<Date>()

    val remindYear = MutableLiveData<Int>()
    val remindMonth = MutableLiveData<Int>()
    var remindDay = MutableLiveData<Int>()
    val remindHour = MutableLiveData<Int>()
    var remindMinute = MutableLiveData<Int>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoaded = false

    private var taskCompleted = false

    fun start(taskId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        val yearObserverFun = createSourceFunction(Calendar.YEAR)
        val monthObserverFun = createSourceFunction(Calendar.MONTH)
        val dayObserverFun = createSourceFunction(Calendar.DAY_OF_MONTH)
        val hourObserverFun = createSourceFunction(Calendar.HOUR_OF_DAY)
        val minuteObserverFun = createSourceFunction(Calendar.MINUTE)

        remindDate.addSource(remindYear, Observer(yearObserverFun))
        remindDate.addSource(remindMonth, Observer(monthObserverFun))
        remindDate.addSource(remindDay, Observer(dayObserverFun))
        remindDate.addSource(remindHour, Observer(hourObserverFun))
        remindDate.addSource(remindMinute, Observer(minuteObserverFun))

        this.taskId = taskId
        if (taskId == null) {
            // No need to populate, it's a new task
            isNewTask = true
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data.
            return
        }

        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            tasksRepository.getTask(taskId).let { result ->
                if (result is Success) {
                    onTaskLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    fun stop() {
        remindDate.removeSource(remindYear)
        remindDate.removeSource(remindMonth)
        remindDate.removeSource(remindDate)
        remindDate.removeSource(remindHour)
        remindDate.removeSource(remindMinute)
    }

    private fun createSourceFunction(fieldType: Int): (Int) -> Unit {
        return fun(newFieldValue: Int) {
            val remindCalendar = Calendar.getInstance()
            remindDate.value?.let { remindCalendar.time = remindDate.value }
            remindCalendar.set(fieldType, newFieldValue)
            remindDate.value = remindCalendar.time
        }
    }

    private fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        val localRemindDate = task.remindDate
        remindDate.value = localRemindDate
        val remindCalendar = Calendar.getInstance()
        localRemindDate?.let {
            remindCalendar.time = localRemindDate
            remindYear.value = remindCalendar.get(Calendar.YEAR)
            remindMonth.value = remindCalendar.get(Calendar.MONTH)
            remindDay.value = remindCalendar.get(Calendar.DAY_OF_MONTH)
            remindHour.value = remindCalendar.get(Calendar.HOUR_OF_DAY)
            remindMinute.value = remindCalendar.get(Calendar.MINUTE)
        }

        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab.
    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        val currentRemindYear = remindYear.value
        val currentRemindMonth = remindMonth.value
        val currentRemindDay = remindDay.value
        val currentRemindHour = remindHour.value
        val currentRemindMinute = remindMinute.value

        val currentCalendar = Calendar.getInstance()

        var calendarUpdated = false
        if (currentRemindDay != null && currentRemindMonth != null && currentRemindYear != null) {
            currentCalendar.set(currentRemindYear, currentRemindMonth, currentRemindDay)
            calendarUpdated = true
        }

        if (currentRemindHour != null && currentRemindMinute != null) {
            currentCalendar.set(Calendar.HOUR_OF_DAY, currentRemindHour)
            currentCalendar.set(Calendar.MINUTE, currentRemindMinute)
            calendarUpdated = true
        }

        var currentRemindDate: Date? = null
        if (calendarUpdated)
            currentRemindDate = currentCalendar.time

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }
        if (Task(currentTitle, currentDescription, currentRemindDate).isEmpty) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentTitle, currentDescription, currentRemindDate))
        } else {
            val task = Task(currentTitle, currentDescription, currentRemindDate, taskCompleted, currentTaskId)
            updateTask(task)
        }
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        tasksRepository.saveTask(newTask)
        _taskUpdatedEvent.value = Event(Unit)
    }

    private fun updateTask(task: Task) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        viewModelScope.launch {
            tasksRepository.saveTask(task)
            _taskUpdatedEvent.value = Event(Unit)
        }
    }
}
