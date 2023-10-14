package com.androidatc.taskscheduler.ui.TaskList


import com.google.gson.Gson
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.androidatc.taskscheduler.R
import com.androidatc.taskscheduler.Task

import com.androidatc.taskscheduler.databinding.FragmentTaskListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

/*
Author: Scott Field
Date: 10/13/2023
Version: 1.0
Purpose:
The Fragment Here Is Meant To Be Used To Display The Task List,
The User Selects A Task By Clicking On The Item In The ListView
After Selecting A The User Can Choose To Edit (Transfer To CreateTaskFragment)
Or Delete The Selected Task
*/

//Create A Custom Adapter To Handle Convert The Task List Into A View (To Display The Task List)
private class TaskAdapter(context: Context, tasks: MutableList<Task>) : ArrayAdapter<Task>(context, android.R.layout.simple_list_item_2, tasks) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = getItem(position)
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        }
        val nameTextView = view!!.findViewById<TextView>(android.R.id.text1)
        val descriptionTextView = view.findViewById<TextView>(android.R.id.text2)
        nameTextView.text = task?.name
        descriptionTextView.text = task?.description
        return view
    }
}

class TaskListFragment : Fragment() {

    //Store The Selected Task Item In A Global Variable
    companion object{
        //Default Is Null Because On Start No Task Is Selected
        var selectedTask: Task? = null
    }

    //Convert the map from sharedTaskStorage to a list the program can use
    fun convertMapToTaskList(sharedTaskStorage: SharedPreferences?): MutableList<Task> {
        //Convert the storage into a key value pair map (Strings get tasks)
        var taskMap: MutableMap<String, *>? = sharedTaskStorage?.all
        Log.d("The map on conversion",taskMap.toString())

        val taskList = mutableListOf<Task>()
        if (taskMap != null && taskMap.isNotEmpty()) {
            for ((key, value) in taskMap) {
                // Use Gson to convert each value in the Map to a Task object
                val gson = Gson()
                val task = gson.fromJson(value.toString(), Task::class.java)
                // Add the Task object to the list
                taskList.add(task)
            }
        }
        return taskList
    }

    //get the FragmentTask Binding
    private var _binding: FragmentTaskListBinding? = null

    //convert _binding to binding for ease of use
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Create / Get If Already Created The Task Storage File (Used To Store The Task List)
        val sharedTaskStorage = activity?.getSharedPreferences("sharedTaskStorage", Context.MODE_PRIVATE)
        //Set the task list to a mutable list of Tasks
        var taskList = mutableListOf<Task>()
        //get the task list
        taskList = convertMapToTaskList(sharedTaskStorage)
        //check the task list
        Log.d("taskList",taskList.toString())

        //Inflate the fragment in the container
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Get The List Of Tasks
        val listView = binding.taskDisplayList
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        //If The List Of Tasks Contains Any Tasks
        if (taskList.size > 0) {
            //Hide The No Tasks Have Been Created Message
            binding.noTasks.visibility = View.INVISIBLE
            //Create An Adapter To Display The List Of Tasks
            var adapter = TaskAdapter(requireContext(), taskList)
            //Add The Adapter To The List Of Tasks
            listView.adapter = adapter
        //Otherwise
        }else{
            //Show the message that no tasks have been created
            binding.noTasks.visibility = View.VISIBLE
        }


        //Get The Edit And Delete Buttons
        val taskDeleteBtn = binding.taskDeleteBtn
        val taskEditBtn = binding.taskEditBtn

        //Event Listener Section Start

        //Set the button to delete a task on click
        taskDeleteBtn.setOnClickListener(){
            //If The User Has Selected A Task To Delete
            if (selectedTask != null){
                //Set The Editor
                val editor = sharedTaskStorage?.edit()
                //Remove The Task By Its Id From Storage
                editor?.remove(selectedTask!!.id)
                //Apply The Change
                editor?.apply()
                //Log The Change
                Log.d("Deleted Task","Deleted Task Displayed Below")
                selectedTask!!.log()
                //Set The Selected Task To Null (Because It Was Just Removed)
                selectedTask = null
                //Update The Page To Reflect The Change
                taskList = convertMapToTaskList(sharedTaskStorage)
                val adapter = TaskAdapter(requireContext(), taskList)
                binding.taskDisplayList.adapter = adapter
                //if the task list is empty
                if (taskList.size == 0){
                    //Show the please add Tasks Message
                    binding.noTasks.visibility = View.VISIBLE
                    //Otherwise
                }else{
                    //Hide the please add Tasks Message
                    binding.noTasks.visibility = View.INVISIBLE
                }
            //If The User Has Selected To Delete A Task Before Selecting A Task
            }else{
                //Output A Message To The User Indicating The User Needs To Select A Task First
                Snackbar.make(binding.root,"Please Select A Task To Delete",Snackbar.LENGTH_SHORT).show()
                Log.d("Task Delete Error","No Task Selected To Delete")
            }
        }

        //set the button to edit the task on click
        taskEditBtn.setOnClickListener {
            //if the user has selected a task to edit
            if (selectedTask != null){
                //First get the bottom navigation view
                val navView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
                //next set the task creation page to be selected
                navView?.selectedItemId = R.id.task_create

            //If The User Has Selected To Edit A Task Before Selecting A Task
            }else{
                //Output A Message To The User Indicating The User Needs To Select A Task First
                Snackbar.make(binding.root,"Please Select A Task To Edit",Snackbar.LENGTH_SHORT).show()
                Log.d("Task Edit Error", "No Task Selected To Edit")
            }
        }

        //Set the Last Clicked View (In order to change the background color back after the same or another list item is selected again)
        var lastClickedView: View? = null

        //Set A Listener For When A Task Is Clicked On
        listView.setOnItemClickListener { parent, view, position, id ->
            // set the background color of the previously clicked view to white
            lastClickedView?.setBackgroundColor(Color.WHITE)
            //if the user did not select the same task again
            if (selectedTask != taskList[position]) {
                // set the selected item to light gray
                view.setBackgroundColor(Color.LTGRAY)
                // get the task the user clicked on
                selectedTask = taskList[position]
                // and log the task
                selectedTask?.log()
                // update the last clicked view
                lastClickedView = view

            //if the user has selected the same task twice unselect the task
            }else{
                selectedTask = null
                lastClickedView = null
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Ensure that on resume the page is updated
    override fun onResume() {
        super.onResume()
        //Create / Get If Already Created The Task Storage File (Used To Store The Task List)
        val sharedTaskStorage = activity?.getSharedPreferences("sharedTaskStorage", Context.MODE_PRIVATE)
        //Set the task list to a mutable list of Tasks
        var taskList = mutableListOf<Task>()
        //get the task list
        taskList = convertMapToTaskList(sharedTaskStorage)
        val adapter = TaskAdapter(requireContext(), taskList)
        binding.taskDisplayList.adapter = adapter
        //if the task list is empty
        if (taskList.size == 0){
            //Show the please add Tasks Message
            binding.noTasks.visibility = View.VISIBLE
        //Otherwise
        }else{
            //Hide the please add Tasks Message
            binding.noTasks.visibility = View.INVISIBLE
        }
        //Ensure that after leaving the screen the last selected task is unselected after you go back
        selectedTask = null
    }

}



