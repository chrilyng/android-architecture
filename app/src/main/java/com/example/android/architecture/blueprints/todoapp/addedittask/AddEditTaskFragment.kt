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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.tasks.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.getViewModelFactory
import com.example.android.architecture.blueprints.todoapp.util.setupRefreshLayout
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dk.siit.todoschedule.R
import dk.siit.todoschedule.databinding.AddtaskFragBinding

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment() {

    private lateinit var viewDataBinding: AddtaskFragBinding

    private val args: AddEditTaskFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTaskViewModel> { getViewModelFactory() }

    private lateinit var localChildFragmentManager: FragmentManager

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        viewDataBinding = AddtaskFragBinding.bind(root).apply {
            this.viewmodel = viewModel
        }
        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        localChildFragmentManager = requireFragmentManager()
        setupSnackbar()
        setupNavigation()
        setupDatePickerButton()
        setupTimePickerButton()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start(args.taskId)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.taskUpdatedEvent.observe(this, EventObserver {
            val action = AddEditTaskFragmentDirections
                    .actionAddEditTaskFragmentToTasksFragment(ADD_EDIT_RESULT_OK)
            findNavController().navigate(action)
        })
    }

    private fun showDatePickerDialog() {
        TaskDatePickerFragment(viewModel).show(childFragmentManager, "datePicker")
    }

    private fun setupDatePickerButton() {
        activity?.findViewById<TextView>(R.id.add_task_remind_date_text)?.let {
            it.setOnClickListener {
                showDatePickerDialog()
            }
        }
    }

    private fun showTimePickerDialog() {
        TaskTimePickerFragment(viewModel).show(childFragmentManager, "timePicker")
    }

    private fun setupTimePickerButton() {
        activity?.findViewById<TextView>(R.id.add_task_remind_time_text)?.let {
            it.setOnClickListener {
                showTimePickerDialog()
            }
        }
    }
}
