package com.example.todone

import Taskk
import android.view.View
import android.widget.Toast
import com.example.todone.databinding.TaskItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class TaskItem(val task:Task, val taskDao:TaskDao): BindableItem<TaskItemBinding>(){
    override fun bind(viewBinding: TaskItemBinding, position: Int) {
        viewBinding.tiTitle.text = task.title
        viewBinding.tiDate.text = task.date
        viewBinding.tiTime.text = task.time
        viewBinding.tiDescription.text = task.description
        viewBinding.tiCheckBox.isChecked = task.done
        viewBinding.tiDeleteButton.setOnClickListener {
            taskDao.deleteTask(task)
        }
        viewBinding.tiCheckBox.setOnClickListener {
            task.done=viewBinding.tiCheckBox.isChecked
            taskDao.upsertTask(task)
        }
    }

    override fun getLayout(): Int {
        return R.layout.task_item
    }

    override fun initializeViewBinding(view: View): TaskItemBinding {
        return  TaskItemBinding.bind(view)
    }
}