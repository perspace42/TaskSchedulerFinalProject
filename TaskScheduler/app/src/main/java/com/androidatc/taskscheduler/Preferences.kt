package com.androidatc.taskscheduler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.androidatc.taskscheduler.databinding.FragmentCreateTaskBinding

/*
Author: Scott Field
Date: 10/13/2023
Version: 1.0
Purpose:
The Fragment Here Is Meant To Be Used To Display A Dropdown
That Allows The User To Select The App Themes (Current Themes Are Purple And Green)
On Different Selection The App Relaunches The Main Activity To Reload The Theme
*/

class Preferences : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate The Fragment
        val view = inflater.inflate(R.layout.fragment_preferences, container, false)
        //get the dropdown
        val themeDropdown = view.findViewById<Spinner>(R.id.themeDropdown)
        // Assign Shared Preferences (Unless The Activity Is Null)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        // Assign and editor to allow the changing of themes
        val editor = sharedPref?.edit()
        //get the current theme from the Shared Preferences (values/strings.xml file)
        var currentTheme = sharedPref?.getString("theme_key","Purple")

        //set the items in the dropdown
        val items = arrayListOf("Purple","Green")
        //set the adapter for the dropdown
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        //add the adapter to the dropdown
        themeDropdown.adapter = adapter

        //get the position of the currently selected theme
        var position = adapter.getPosition(currentTheme)
        //set the theme in the dropdown to the currently selected item
        themeDropdown.setSelection(position)


        //Set The ThemeDropdown To Change The Theme If A Different Theme Is Selected
        themeDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item from the Spinner
                val selectedItem = parent.getItemAtPosition(position) as String

                // Change the theme based on the selected item (if it is different than the current theme
                when (selectedItem) {
                    "Purple" -> {
                        if (currentTheme != "Purple") {
                            //set the new current theme in the strings.xml file
                            editor?.putString("theme_key", "Purple")
                            editor?.apply()

                            //then change the theme across the entire app
                            val intent = Intent(activity, MainActivity::class.java).apply {
                                putExtra("themeId", R.style.Theme_TaskScheduler)
                            }
                            activity?.startActivity(intent)
                        }
                    }

                    "Green" -> {
                        if (currentTheme != "Green") {
                            //set the new current theme in the strings.xml file
                            editor?.putString("theme_key", "Green")
                            editor?.apply()

                            //then change the theme across the entire app
                            val intent = Intent(activity, MainActivity::class.java).apply {
                                putExtra("themeId", R.style.Theme_Green)
                            }
                            activity?.startActivity(intent)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing (function must be overriden)
            }
        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}