package com.example.todone

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todone.databinding.ActivityEditTaskBinding
import com.example.todone.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskActivity() : AppCompatActivity() {
    lateinit var binding:ActivityEditTaskBinding
    lateinit var db :TasksDB
    lateinit var taskDao:TaskDao
    lateinit var coder: Geocoder
    val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.taskDateUp.setOnClickListener {
            showDatePicker()
        }
        binding.taskTimeUp.setOnClickListener {
            showTimePicker()
        }

        binding.updateTaskButton.setOnClickListener{
            updateTask()
        }

        db = TasksDB.getDatabase(this)
        taskDao = db.taskDao()


        binding.taskTitleUp.setText(intent.getStringExtra("title"))
        binding.TaskDescriptionUp.setText(intent.getStringExtra("description"))
        binding.taskDateUp.setText(intent.getStringExtra("date"))
        binding.taskTimeUp.setText(intent.getStringExtra("time"))
        binding.taskLocationUp.setText(intent.getStringExtra("location"))

//        val task=Task(taskId,binding.taskTitleUp.text.toString(),binding.TaskDescriptionUp.text.toString(),
//            binding.taskDateUp.text.toString(), binding.taskTimeUp.text.toString(), false,
//                taskLoc.first().latitude, taskLoc.first().longitude, binding.taskLocationUp.text.toString(),false)
    }

    fun updateTask(){
        if(binding.taskTitleUp.text.toString().isEmpty() || binding.TaskDescriptionUp.text.toString().isEmpty() ||
            binding.taskDateUp.text.toString().isEmpty() || binding.taskTimeUp.text.toString().isEmpty()){
            Toast.makeText(this, "please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val taskId=intent.getIntExtra("id",0)
        if (binding.taskLocationUp.text.toString().isNotEmpty()) {
            coder= Geocoder(this)
        try {
                val taskLoc = coder.getFromLocationName(binding.taskLocationUp.text.toString(), 1)
                if (!taskLoc.isNullOrEmpty()) {   // adding task with location
                    val task= Task(taskId,binding.taskTitleUp.text.toString(), binding.TaskDescriptionUp.text.toString(),
                        binding.taskDateUp.text.toString(),binding.taskTimeUp.text.toString(),false,
                        taskLoc.first().latitude, taskLoc.first().longitude, binding.taskLocationUp.text.toString())
                    taskDao.upsertTask(task)
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_LONG).show()
                } else {
                    val task= Task(taskId,binding.taskTitleUp.text.toString(), binding.TaskDescriptionUp.text.toString(),
                        binding.taskDateUp.text.toString(),binding.taskTimeUp.text.toString(),false,
                        locationLat = 0.0, locationLong = 0.0,binding.taskLocationUp.text.toString())
                    taskDao.upsertTask(task)
                    Toast.makeText(this, "Can't find location,Task updated successfully", Toast.LENGTH_LONG).show()
                }
            }catch (e:Exception){
                Toast.makeText(this, "Connection Error!", Toast.LENGTH_LONG).show()
            }
    }else{
            val task= Task(taskId,binding.taskTitleUp.text.toString(),binding.TaskDescriptionUp.text.toString(),
                binding.taskDateUp.text.toString(),binding.taskTimeUp.text.toString(),false,
                locationLat = 0.0, locationLong = 0.0,location = binding.taskLocationUp.text.toString())
            taskDao.upsertTask(task)
            Toast.makeText(this, "Task updated successfully without location", Toast.LENGTH_LONG).show()
        }
    }

    fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dateString = "$selectedYear-${selectedMonth+1}-$selectedDay"
            binding.taskDateUp.setText(dateString)
        }, year, month, day) // default date
        datePickerDialog.show()
    }

    fun showTimePicker(){
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePicker = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Convert selected time to 12-hour format string
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val timeString = timeFormat.format(calendar.time)

                binding.taskTimeUp.setText(timeString)
            },
            hour, minute, false // 24-hour format (t/f)
        )
        timePicker.show()
    }
}