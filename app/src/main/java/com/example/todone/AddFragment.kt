package com.example.todone

import Taskk
import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.todone.databinding.FragmentAddBinding
import mumayank.com.airlocationlibrary.AirLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class AddFragment : Fragment(), AirLocation.Callback {
    lateinit var binding:FragmentAddBinding
    lateinit var airLocation: AirLocation
    lateinit var coder:Geocoder
    lateinit var db :TasksDB
    lateinit var taskDao:TaskDao
    val tasks= mutableListOf<Taskk>()
    val calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=FragmentAddBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // write main code here
        binding.addTaskButton.setOnClickListener {
            addTask(view)
        }
        binding.taskDate.setOnClickListener {
            showDatePicker()
        }
        binding.taskTime.setOnClickListener {
            showTimePicker()
        }
        airLocation=AirLocation(requireActivity(),this,true)
        airLocation.start()
        coder= Geocoder(requireContext())
        db = TasksDB.getDatabase(requireContext())
        taskDao = db.taskDao()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode,resultCode,data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // Notifications
//                val request=OneTimeWorkRequest.Builder(NotificationWork::class.java)
//                    .build()
                val request=PeriodicWorkRequest.Builder(NotificationWork::class.java,15,TimeUnit.MINUTES)
                    .build()
                val requestLocationNotification=PeriodicWorkRequest.Builder(LocationNotificationWork::class.java,15,TimeUnit.MINUTES)
                    .build()

                WorkManager.getInstance(requireContext()).enqueue(request)
                WorkManager.getInstance(requireContext()).enqueue(requestLocationNotification)
            }
        }
    }

    fun addTask(view: View) {
        if(binding.taskTitle.text.toString().isEmpty() || binding.taskDescription.text.toString().isEmpty() ||
            binding.taskDate.text.toString().isEmpty() || binding.taskTime.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

//        val time12h= SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(binding.taskTime.text.toString())
//        val time24h=SimpleDateFormat("HH:mm", Locale.getDefault()).format(time12h)
//        println(time24h)
        if (binding.taskLocation.text.toString().isNotEmpty()) {
            try {
                val taskLoc = coder.getFromLocationName(binding.taskLocation.text.toString(), 1)
                if (!taskLoc.isNullOrEmpty()) {   // adding task with location
//                    tasks.add(
//                        Taskk(binding.taskTitle.text.toString(), binding.taskDescription.text.toString(), binding.taskDate.text.toString(),
//                            binding.taskTime.text.toString(), false, taskLoc.first().latitude, taskLoc.first().longitude))
                    val newTask= Task(title = binding.taskTitle.text.toString(), description =  binding.taskDescription.text.toString(),
                        date = binding.taskDate.text.toString(), time = binding.taskTime.text.toString(), done = false,
                        locationLat = taskLoc.first().latitude, locationLong = taskLoc.first().longitude, location = binding.taskLocation.text.toString())
                    taskDao.upsertTask(newTask)
                    Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_LONG).show()
                } else {
//                    tasks.add(Taskk(binding.taskTitle.text.toString(), binding.taskDescription.text.toString(), binding.taskDate.text.toString(),
//                        binding.taskTime.text.toString(), false, 0.0, 0.0))
                    val newTask= Task(title = binding.taskTitle.text.toString(), description =  binding.taskDescription.text.toString(),
                        date = binding.taskDate.text.toString(), time = binding.taskTime.text.toString(), done = false,
                        locationLat = 0.0, locationLong = 0.0,location = binding.taskLocation.text.toString())
                    taskDao.upsertTask(newTask)
                    Toast.makeText(requireContext(), "Can't find location,Task added successfully", Toast.LENGTH_LONG).show()
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "Connection Error!", Toast.LENGTH_LONG).show()
            }

        }else{
//            tasks.add(Taskk(binding.taskTitle.text.toString(), binding.taskDescription.text.toString(), binding.taskDate.text.toString(),
//                binding.taskTime.text.toString(), false, 0.0, 0.0))
            val newTask= Task(title = binding.taskTitle.text.toString(), description =  binding.taskDescription.text.toString(),
            date = binding.taskDate.text.toString(), time = binding.taskTime.text.toString(), done = false,
            locationLat = 0.0, locationLong = 0.0,location = binding.taskLocation.text.toString())
            taskDao.upsertTask(newTask)
            Toast.makeText(requireContext(), "Task added successfully without location", Toast.LENGTH_LONG).show()
        }
//        println(tasks.first().locationLat+tasks.first().locationLong)

        // Notifications permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS),1)
        }else{
            // Notifications
//            val request=OneTimeWorkRequest.Builder(NotificationWork::class.java)
//                .build()

            val request=PeriodicWorkRequest.Builder(NotificationWork::class.java,15,TimeUnit.MINUTES)
                .build()
            val requestLocationNotification=PeriodicWorkRequest.Builder(LocationNotificationWork::class.java,15,TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(request)
            WorkManager.getInstance(requireContext()).enqueue(requestLocationNotification)
        }


    }

    fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val dateString = "$selectedYear-${selectedMonth+1}-$selectedDay"
            binding.taskDate.setText(dateString)
        }, year, month, day) // default date

        datePickerDialog.show()
    }

    fun showTimePicker(){
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // Convert selected time to 12-hour format string
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val timeString = timeFormat.format(calendar.time)

                binding.taskTime.setText(timeString)
            },
            hour, minute, false // 24-hour format (t/f)
        )

        timePicker.show()
    }


    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
        if (isAdded) {  // check if the fragment is still attached
        Toast.makeText(requireContext(), "Error getting location!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSuccess(locations: ArrayList<Location>) {
        val lat=locations.first().latitude
        val long=locations.first().longitude
        val address=coder.getFromLocation(lat,long,1)
        if(!address.isNullOrEmpty()){
            binding.taskLocation.setText(address.first().featureName+", "+address.first().thoroughfare+", "+address.first().locality)
            println(address.first().getAddressLine(0))
        }

    }


}