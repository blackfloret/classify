package com.example.classify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classify.databinding.TodoItemBinding
import com.example.classify.placeholder.PlaceholderContent.PlaceholderItem

// Define a listener interface for this fragment
interface OnTodoClicked {
    fun onTodoClick(position: Int)
}

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyTodoListRecyclerViewAdapter(
    private val values: List<ToDoData>,
) : RecyclerView.Adapter<MyTodoListRecyclerViewAdapter.ViewHolder>() {

    var onClick: OnTodoClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TodoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        var minStr = ""
        if (item.minute < 10) {
            minStr = "0" + item.minute.toString()
        } else {
            minStr = item.minute.toString()
        }

        var timeStr = ""
        if (item.hour == 12 ) {
            timeStr = timeStr + 12.toString() + ":" + minStr + " PM"
        } else if (item.hour == 0) {
            timeStr = timeStr + 12.toString() + ":" + minStr + " AM"
        } else if (item.hour > 12) {
            timeStr = timeStr + (item.hour - 12).toString() + ":" + minStr + " PM"
        } else {
            timeStr = timeStr + item.hour.toString() + ":" + minStr + " AM"
        }

        holder.timeView.text = timeStr
        holder.nameView.text = item.priority.toString() + ". " + item.name
        holder.dateView.text = item.date.toString()
        holder.commentView.text = item.comment
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.todoName
        val dateView: TextView = binding.todoDate
        val timeView: TextView = binding.todoTime
        val commentView: TextView = binding.todoComment
    }

}