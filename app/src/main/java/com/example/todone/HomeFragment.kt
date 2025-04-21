package com.example.todone

import Task
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todone.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupieAdapter
import java.time.LocalDate


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    lateinit var tasks:MutableList<Task>
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
        tasks= mutableListOf(Task("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false),
            Task("Make a call","call Mr.Amr", "2025-04-20","10:30 PM",false),
            Task("Gym","x10 push ups and 10 ABS\nRunning 10 mins", "2025-04-20","10:30 PM",false),
            Task("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false),
            Task("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false),
            Task("Gym","x10 push ups and 10 ABS", "2025-04-20","10:30 PM",false),)

        val taskItems=tasks.map { TaskItem(it) }
        val adapter= GroupieAdapter()
        adapter.addAll(taskItems)
        println(taskItems)
        binding.rvTaks.adapter=adapter
        }
    }