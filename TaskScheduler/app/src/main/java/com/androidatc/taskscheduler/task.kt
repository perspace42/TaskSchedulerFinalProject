package com.androidatc.taskscheduler
import android.util.Log
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID

/*
Author: Scott Field
Last Modified: 09/15/2023
Purpose:
Create a class with which to store the tasks the program will organize in lists
 */


//Must inherit Serializable so that the Task class can be passed in an intent (and can be saved to a file)
class Task : Serializable {
    //define variables for the class
    var id = UUID.randomUUID().toString() //Create A Random Id Number To Pull The Class Data Using
    var name = ""
    var description = ""
    var creationDate = LocalDate.now().toString()
    var startDate = ""
    var endDate = ""
    /*
    In Ascending Order Priority Value is Either:
        Trivial , Moderate , Critical
    */
    var priority = ""
    var status = "";

    //Constructor With Arguments
    constructor(name: String, description: String, startDate: String,
                endDate: String, priority: String, status: String) {
        this.name = name
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
        this.priority = priority
        this.status = status
    }

    //Default Constructor For Gson (Warning Message Said It Was Needed)
    constructor()

    //Check A Created Task
    fun log(){
        Log.d("Logged Task",
              "id: $id \n" +
                   "name: $name\n" +
                  "description: $description\n" +
                  "creationDate: ${creationDate.toString()}\n" +
                  "startDate: $startDate\n" +
                  "endDate:  $endDate\n" +
                  "priority: $priority\n" +
                  "status: $status")
    }
}