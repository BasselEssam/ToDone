package com.example.todone

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TasksDB:RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object{
        lateinit var database:TasksDB

        fun getDatabase(context: Context):TasksDB{
            database = Room.databaseBuilder(context.applicationContext, TasksDB::class.java,
                "task_db").allowMainThreadQueries().build() // **************
            return database
        }
    }
}