package com.androidatc.taskscheduler.ui.CreateTask


import android.app.DatePickerDialog
import android.content.Context

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.androidatc.taskscheduler.databinding.FragmentCreateTaskBinding

import android.widget.ArrayAdapter
import android.widget.EditText
import com.androidatc.taskscheduler.Task
import com.androidatc.taskscheduler.ui.TaskList.TaskListFragment
import com.google.android.material.snackbar.Snackbar

import com.google.gson.Gson
import java.time.LocalDate

/*
Author: Scott Field
Date: 10/13/2023
Version: 1.0
Purpose:
The Fragment Here Is Meant To Be Used To Allow To Create A New
Task Or Edit An Existing Task, No Edit Or Creation is Final Until the + FloatingActionButton
has been pressed, the <- FloatingActionButton is used to transfer from Edit Mode To Creation Mode
(That Button Is Only Visible In Edit Mode)
In Addition Exiting From This Fragment To Task List Also Switches Create Task Back To Creation Mode
*/


class CreateTaskFragment: Fragment() {
    //Get The Current Task (Used When The User Selects Edit In The TaskListFragment)
    companion object{
        var currentTask: Task? = null
    }

    //Function to use the calendar widget to select start date and end date
    private fun addCalendar(myEditText: EditText){
        //get the Calendar
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //Set The Dialog From The Calendar
        val datePickerDialog = DatePickerDialog(requireContext(),  { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-${dayOfMonth}"
            myEditText.setText(selectedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private var _binding: FragmentCreateTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //set the boolean to check if the user is editing an existing task or creating a new one
    private var inEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate The Fragment
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //set text boxes to default values (can't be done in the attributes tab when null for some reason)
        binding.nameEntry.text = null
        binding.descriptionEntry.text = null

        //set creation date to not be able to be edited and set it to the current date
        val creationDateEntry = binding.creationDateEntry
        //set the creation date to the current date
        creationDateEntry.text = Editable.Factory.getInstance().newEditable(LocalDate.now().toString())
        creationDateEntry.isFocusable = false
        creationDateEntry.isClickable = false

        //clear the other text boxes
        binding.startDateEntry.text = null
        binding.endDateEntry.text = null

        //Assign Values To The Task  Priority Dropdown
        val priorityDropDown = binding.priorityDropDown
        val priorityItems = arrayOf("Trivial","Moderate","Critical")
        priorityDropDown.adapter =  ArrayAdapter(requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item, priorityItems)
        //Assign Values To The Status Dropdown
        val statusDropDown = binding.statusDropDown
        val statusItems = arrayOf("In Progress", "Complete")
        statusDropDown.adapter =  ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, statusItems)

        //Hide The Change Back To Create Task Mode Button On Startup
        binding.goBack.visibility = View.INVISIBLE

        //Assign Functionality To The Submit Button
        var submitButton = binding.submitTask
        submitButton.setOnClickListener {
            //Get The Values For The New Class
            var taskName = binding.nameEntry.text.toString()
            var taskDescription = binding.descriptionEntry.text.toString()
            var startDate = binding.startDateEntry.text.toString()
            var endDate = binding.endDateEntry.text.toString()
            var priority = binding.priorityDropDown.selectedItem.toString()
            var status = binding.statusDropDown.selectedItem.toString()
            //Assign The New Task Values To The Class Object
            var newTask = Task(taskName,taskDescription,startDate,endDate,priority,status)

            //Set a variable to store the snackbar message
            var message: String

            //If the user is editing an existing task
            if (inEditMode == true){
                //Update the id of the new task to match the existing task
                newTask.id = currentTask!!.id
                //Update the creation date of the new task to match the existing task
                newTask.creationDate = currentTask!!.creationDate
                //set the snackbar message that the user successfully edited a task
                message = "task edited successfully"
            //If the user is creating a new task
            }else{
                //set the snackbar message that the user successfully created a task
                message = "task created successfully"
            }


            //Print Out The Task Values In The Log
            newTask.log();

            //get the storage location to send the Task to
            val storageLocation = activity?.getSharedPreferences("sharedTaskStorage", Context.MODE_PRIVATE)
            //set the editor to be able to edit the storage location
            if (storageLocation != null) {
                val editor = storageLocation?.edit()
                //convert the task to JSON
                val taskJson = Gson().toJson(newTask)
                //add the Json String to the SharedPreferences file using the task id
                editor?.putString(newTask.id, taskJson)
                //save the changes to the file
                editor?.apply()
            //check if the location is even being used
            }else {
                Log.d("Error","storage location is null")
            }

            //Output the results of the operation
            Snackbar.make(binding.root,message,Snackbar.LENGTH_SHORT).show()
        }

        //Assign Functionality To The Clear Button
        val clearButton = binding.clearInput
        clearButton.setOnClickListener{
            //clear the text boxes (creation date cannot be cleared by the user)
            binding.nameEntry.text = null;
            binding.descriptionEntry.text = null;
            binding.startDateEntry.text = null;
            binding.endDateEntry.text = null;
            priorityDropDown.setSelection(0)
            statusDropDown.setSelection(0)
        }

        //Assign Functionality To The Go Back Button (Switch From Edit Mode To Create New Task Mode)
        val switchModeButton = binding.goBack
        switchModeButton.setOnClickListener{
            //set the program to create tasks rather than edit existing ones
            inEditMode = false
            //set the text to reflect the change
            binding.taskCreateMode.text = "Currently Creating New Task"
            //hide the go back button
            binding.goBack.visibility = View.INVISIBLE
            //eliminate the current Task
            currentTask = null
            //set the selected task to null
            TaskListFragment.selectedTask = null

            //clear the entries
            binding.nameEntry.text = null;
            binding.descriptionEntry.text = null;
            binding.startDateEntry.text = null;
            binding.endDateEntry.text = null;
            creationDateEntry.text = Editable.Factory.getInstance().newEditable(LocalDate.now().toString())
            priorityDropDown.setSelection(0)
            statusDropDown.setSelection(0)

        }

        // Set OnClickListener for startDateEntry to show DatePickerDialog
        binding.startDateEntry.setOnClickListener {
            addCalendar(binding.startDateEntry)
        }

        // Set OnClickListener for endDateEntry to show DatePickerDialog
        binding.endDateEntry.setOnClickListener {
            addCalendar(binding.endDateEntry)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        //get the task passed by the global variable (if any)
        currentTask = TaskListFragment.selectedTask


        if (currentTask != null){
            //set the program to be in edit mode
            inEditMode = true
            //set the text to show the current operation
            binding.taskCreateMode.text = "Currently Editing Old Task"
            //set the button to be able to switch back
            binding.goBack.visibility = View.VISIBLE
            //set the task creation widgets to match the current task data

            //Set The Name And Description
            binding.nameEntry.text = Editable.Factory.getInstance().newEditable(currentTask!!.name)
            binding.descriptionEntry.text = Editable.Factory.getInstance().newEditable(currentTask!!.description)

            //Set The Creation Date Start Date And End Date
            binding.creationDateEntry.text = Editable.Factory.getInstance().newEditable(currentTask!!.creationDate)
            binding.startDateEntry.text = Editable.Factory.getInstance().newEditable(currentTask!!.startDate)
            binding.endDateEntry.text = Editable.Factory.getInstance().newEditable(currentTask!!.endDate)

            //Set The Priority Dropdown
            var adapter = binding.priorityDropDown.adapter as ArrayAdapter<String>
            var position = adapter.getPosition(currentTask!!.priority)
            binding.priorityDropDown.setSelection(position)

            //Set The Status Dropdown
            adapter = binding.statusDropDown.adapter as ArrayAdapter<String>
            position = adapter.getPosition(currentTask!!.status)
            binding.statusDropDown.setSelection(position)
        }
    }
}

