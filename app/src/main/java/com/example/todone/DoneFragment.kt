package com.example.todone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todone.databinding.FragmentDoneBinding
import com.example.todone.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupieAdapter

class DoneFragment : Fragment() {
    lateinit var binding: FragmentDoneBinding
    lateinit var doneTasks:MutableList<Task>
    lateinit var db :TasksDB
    lateinit var taskDao:TaskDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentDoneBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = TasksDB.getDatabase(requireContext())
        taskDao = db.taskDao()
        doneTasks=taskDao.getDoneTasks()

        val taskItems=doneTasks.map { TaskItem(it,taskDao) }
        val adapter=GroupieAdapter()
        adapter.addAll(taskItems)
        println(taskItems)
        binding.doneTaskRV.adapter=adapter
    }

}