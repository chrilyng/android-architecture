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

        remindDate.addSource(remindYear, Observer(yearObserverFun))
        remindDate.addSource(remindMonth, Observer(monthObserverFun))
        remindDate.addSource(remindDay, Observer(dayObserverFun))

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
        remindCalendar.time = localRemindDate
        remindDay.value = remindCalendar.get(Calendar.DATE)
        remindMonth.value = remindCalendar.get(Calendar.MONTH)
        remindYear.value = remindCalendar.get(Calendar.YEAR)

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

        var currentRemindDate = Date()
        if (currentRemindDay != null && currentRemindMonth != null && currentRemindYear != null)
            currentRemindDate = GregorianCalendar(currentRemindYear, currentRemindMonth, currentRemindDay).time

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
