package com.example.todone

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface TaskDao {

        @Upsert    // update an existing id or create new one
        fun upsertTask(task: Task)

        @Query("select * from task")
        fun getTasks():MutableList<Task>

        @Delete
        fun deleteTask(task:Task)

        @Query("select * from task where done=1")
        fun getDoneTasks():MutableList<Task>

        @Query("select * from task where done=0 AND notifiedBefore=0 AND date = :currentDate")
        fun getNotifTasks(currentDate: String):MutableList<Task>

        @Query("select * from task where done=0 AND notifiedLocation=0 AND locationLat!=0.0")
        fun getNotifLocationTasks():MutableList<Task>

}