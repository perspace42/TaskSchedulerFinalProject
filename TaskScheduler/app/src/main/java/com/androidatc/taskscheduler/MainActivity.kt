package com.androidatc.taskscheduler

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.androidatc.taskscheduler.databinding.ActivityMainBinding
import androidx.constraintlayout.widget.ConstraintLayout

/*
Author: Scott Field
Date: 10/13/2023
Version: 1.0
Purpose:
The Activity Here Is Meant To Be Used To Load The App Navigation Bar And Set The App Theme
*/

class MainActivity : AppCompatActivity() {
    //variable to store the integer value of the theme
    var themeId = 0

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //Getting And Loading The Theme From The Intent (If None Present Defaults To Purple)

        // Load the saved theme from the shared preferences
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val currentTheme = sharedPreferences.getString("theme_key", "Purple")

        // Get the integer value for the theme from the intent or use the default (purple) theme if no theme is provided
        val themeId = intent.getIntExtra(
            "themeId", when (currentTheme) {
                "Green" -> R.style.Theme_Green
                else -> R.style.Theme_TaskScheduler
            }
        )
        setTheme(themeId)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setting The NavView

        val navView: BottomNavigationView = binding.navView

        /*Start Code To Fix Slow Loading Problem*/
        val layout: ConstraintLayout = findViewById(R.id.container)
        val view: BottomNavigationView = findViewById(R.id.nav_view)

        view.setOnClickListener {
            TransitionManager.beginDelayedTransition(layout)
            view.visibility = View.GONE
        }
        /*End Code To Fix Slow Loading Problem*/

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            //This is necessary to ensure that the back arrow does not display at the top menu
            setOf(
                R.id.task_list, R.id.task_create, R.id.help, R.id.preferences
            )
        )

        //Setting The NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //added event listener to fix out of scope problem (update it worked)
        navView.setOnItemSelectedListener { item ->
            //Ensures That Whenever An Item Is Selected The Activity Navigates To It And Updates The NavBar
            when (item.itemId) {
                R.id.task_list -> {
                    navController.navigate(R.id.task_list)
                    true
                }
                R.id.task_create ->{
                    navController.navigate(R.id.task_create)
                    true
                }
                R.id.preferences ->{
                    navController.navigate(R.id.preferences)
                    true
                }
                R.id.help ->{
                    navController.navigate(R.id.help)
                    true
                }
                //If (Somehow) An Item in the navbar is not defined and clicked on set nothing to happen
                else -> false
            }
        }

    }
}