package com.example.todone

import Taskk
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todone.databinding.FragmentAddBinding
import com.google.android.gms.tasks.Task
import mumayank.com.airlocationlibrary.AirLocation

class AddFragment : Fragment(), AirLocation.Callback {
    lateinit var binding:FragmentAddBinding
    lateinit var airLocation: AirLocation
    lateinit var coder:Geocoder
    lateinit var db :TasksDB
    lateinit var taskDao:TaskDao
    val tasks= mutableListOf<Taskk>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // write main code here
        binding.addTaskButton.setOnClickListener {
            addTask(view)
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
    }

    fun addTask(view: View) {
        if(binding.taskTitle.text.toString().isEmpty() || binding.taskDescription.text.toString().isEmpty() ||
            binding.taskDate.text.toString().isEmpty() || binding.taskTime.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.taskLocation.text.toString().isNotEmpty()) {
            try {
                val taskLoc = coder.getFromLocationName(binding.taskLocation.text.toString(), 1)
                if (!taskLoc.isNullOrEmpty()) {   // adding task with location
//                    tasks.add(
//                        Taskk(binding.taskTitle.text.toString(), binding.taskDescription.text.toString(), binding.taskDate.text.toString(),
//                            binding.taskTime.text.toString(), false, taskLoc.first().latitude, taskLoc.first().longitude))
                    val newTask= Task(title = binding.taskTitle.text.toString(), description =  binding.taskDescription.text.toString(),
                        date = binding.taskDate.text.toString(), time = binding.taskTime.text.toString(), done = false,
                        locationLat = taskLoc.first().latitude, locationLong = taskLoc.first().longitude)
                    taskDao.upsertTask(newTask)
                    Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_LONG).show()
                } else {
//                    tasks.add(Taskk(binding.taskTitle.text.toString(), binding.taskDescription.text.toString(), binding.taskDate.text.toString(),
//                        binding.taskTime.text.toString(), false, 0.0, 0.0))
                    val newTask= Task(title = binding.taskTitle.text.toString(), description =  binding.taskDescription.text.toString(),
                        date = binding.taskDate.text.toString(), time = binding.taskTime.text.toString(), done = false,
                        locationLat = 0.0, locationLong = 0.0)
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
            locationLat = 0.0, locationLong = 0.0)
            taskDao.upsertTask(newTask)
            Toast.makeText(requireContext(), "Task added successfully without location", Toast.LENGTH_LONG).show()
        }
//        println(tasks.first().locationLat+tasks.first().locationLong)

    }

    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
        Toast.makeText(requireContext(), "Error getting location!", Toast.LENGTH_LONG).show()
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