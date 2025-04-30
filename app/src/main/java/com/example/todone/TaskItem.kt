package com.example.todone

import android.content.Intent
import android.view.View
import com.example.todone.databinding.TaskItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class TaskItem(val task:Task, val taskDao:TaskDao): BindableItem<TaskItemBinding>(){
    override fun bind(viewBinding: TaskItemBinding, position: Int) {
        viewBinding.tiTitle.text = task.title
        viewBinding.tiDate.text = task.date
        viewBinding.tiTime.text = task.time
        viewBinding.tiDescription.text = task.description
        viewBinding.tiCheckBox.isChecked = task.done
        viewBinding.tiLocation.text=if (task.location.isEmpty()) "" else "\uD83D\uDCCD ${task.location}"
        viewBinding.tiDeleteButton.setOnClickListener {
            taskDao.deleteTask(task)
            task.location
        }
        viewBinding.tiCheckBox.setOnClickListener {
            task.done=viewBinding.tiCheckBox.isChecked
            taskDao.upsertTask(task)
        }
        viewBinding.tiEditButton.setOnClickListener {
            val a=Intent(viewBinding.root.context,EditTaskActivity::class.java)
            a.putExtra("id",task.id)
            a.putExtra("title",task.title)
            a.putExtra("description",task.description)
            a.putExtra("date",task.date)
            a.putExtra("time",task.time)
            a.putExtra("location",task.location)
            a.putExtra("locationLat",task.locationLat)
            a.putExtra("locationLong",task.locationLong)
            viewBinding.root.context.startActivity(a)
        }
    }

    override fun getLayout(): Int {
        return R.layout.task_item
    }

    override fun initializeViewBinding(view: View): TaskItemBinding {
        return  TaskItemBinding.bind(view)
    }
}