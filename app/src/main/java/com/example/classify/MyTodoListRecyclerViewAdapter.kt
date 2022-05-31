package com.example.classify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classify.databinding.TodoItemBinding
import com.example.classify.placeholder.PlaceholderContent.PlaceholderItem

// Define a listener interface for this fragment
interface OnItemClicked {
    fun onItemClick(position: Int)
}

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyTodoListRecyclerViewAdapter(
    private val values: List<ToDoData>,
) : RecyclerView.Adapter<MyTodoListRecyclerViewAdapter.ViewHolder>() {

    var onClick: OnItemClicked? = null

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
        holder.nameView.text = item.priority.toString() + ". " + item.name
        holder.dateView.text = item.date.toString()
        if (item.hour > 12) {
            holder.timeView.text = (item.hour - 12).toString() + ":" + item.minute + " PM"
        } else if (item.hour == 12) {
            holder.timeView.text = item.hour.toString() + ":" + item.minute + " PM"
        } else if (item.hour == 0) {
            holder.timeView.text = 12.toString() + ":" + item.minute + " AM"
        } else {
            holder.timeView.text = item.hour.toString() + ":" + item.minute + " AM"
        }
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