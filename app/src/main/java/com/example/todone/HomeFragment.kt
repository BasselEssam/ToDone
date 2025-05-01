package com.example.todone

import Taskk
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todone.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupieAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    lateinit var tasks:MutableList<Task>
    lateinit var db :TasksDB
    lateinit var taskDao:TaskDao
    lateinit var adapter: GroupieAdapter
    lateinit var taskItems:List<TaskItem>
    var sortedAsc=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sortTasksText.setOnClickListener {
            sortTasksByDate()
        }
        db = TasksDB.getDatabase(requireContext())
        taskDao = db.taskDao()
//        tasks= mutableListOf(Taskk("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false,0.0,0.0),
//            Taskk("Make a call","call Mr.Amr", "2025-04-20","10:30 PM",false,0.0,0.0),
//            Taskk("Gym","x10 push ups and 10 ABS\nRunning 10 mins", "2025-04-20","10:30 PM",false,0.0,0.0),
//            Taskk("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false,0.0,0.0),
//            Taskk("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false,0.0,0.0),
//            Taskk("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false,0.0,0.0),)
        tasks=taskDao.getTasks()
        taskItems=tasks.map { TaskItem(it,taskDao,{taskToDelete->deleteTask(taskToDelete)}) }
        adapter= GroupieAdapter()
        adapter.addAll(taskItems)
        binding.rvTaks.adapter=adapter
        }

    private fun sortTasksByDate() {
        if (tasks.isNotEmpty()&&!sortedAsc){
            tasks.sortBy {  LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-M-d")) }
            adapter.clear()
            taskItems=tasks.map { TaskItem(it,taskDao,{taskToDelete->deleteTask(taskToDelete)}) }
            adapter.addAll(taskItems)
            Toast.makeText(requireContext(), "Tasks sorted Ascending", Toast.LENGTH_SHORT).show()
            sortedAsc=true
        }
        else if (tasks.isNotEmpty()&& sortedAsc){
            tasks.sortByDescending {  LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-M-d")) }
            adapter.clear()
            taskItems=tasks.map { TaskItem(it,taskDao,{taskToDelete->deleteTask(taskToDelete)}) }
            adapter.addAll(taskItems)
            Toast.makeText(requireContext(), "Tasks sorted Descending", Toast.LENGTH_SHORT).show()
            sortedAsc=false
        }
    }

    private fun deleteTask(taskToDelete: Task) {
            tasks.remove(taskToDelete)
            adapter.clear()
            taskItems=tasks.map { TaskItem(it,taskDao,{taskToDelete->deleteTask(taskToDelete)}) }
            adapter.addAll(taskItems)
    }
}