package com.example.todone

import Task
import android.view.View
import com.example.todone.databinding.TaskItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class TaskItem(val task:Task): BindableItem<TaskItemBinding>(){
    override fun bind(viewBinding: TaskItemBinding, position: Int) {
        viewBinding.tiTitle.text=task.title
        viewBinding.tiDate.text=task.date
        viewBinding.tiTime.text=task.time
        viewBinding.tiDescription.text=task.description
        viewBinding.tiCheckBox.isChecked=task.done
    }

    override fun getLayout(): Int {
        return R.layout.task_item
    }

    override fun initializeViewBinding(view: View): TaskItemBinding {
        return  TaskItemBinding.bind(view)
    }
}