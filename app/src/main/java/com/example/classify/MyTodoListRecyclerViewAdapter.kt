package com.example.classify

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classify.databinding.TodoItemBinding
import com.example.classify.placeholder.PlaceholderContent.PlaceholderItem
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

// Define a listener interface for this fragment
interface TodoListener {
    fun onTodoClick(position: Int)
    fun onTodoRemove(priority: Int)
}

interface BalanceListener {
    fun onAddBalance(value: Int)
}

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyTodoListRecyclerViewAdapter(
    private val values: ArrayList<ToDoData>,
) : RecyclerView.Adapter<MyTodoListRecyclerViewAdapter.ViewHolder>() {

    var todoListener: TodoListener? = null
    var balanceListenerMain: BalanceListener? = null
    var balanceListenerPetCare: BalanceListener? = null
    var balanceListenerSchedule: BalanceListener? = null

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
        var value: Int = -1
        val item = values[position]

        // Remove the todoItem from RecyclerView AND database
        holder.xButton.setOnClickListener {
            balanceListenerMain?.onAddBalance(value)
            balanceListenerPetCare?.onAddBalance(value)
            balanceListenerSchedule?.onAddBalance(value)
            Log.d("recycler todo", "value of removed item = $value")

            todoListener?.onTodoRemove(item.priority)
            Log.d("recycler todo", "priority of removed item = ${item.priority}")

            removeItem(position)
            Log.d("recycler todo", "removed view holder")
        }

        // Calculate the value of the TodoItem
        val today: LocalDate = LocalDate.now()
        var period = Period.between(today, item.date)
        Log.d("recycler todo", "period of days = ${period.days}")

        if (period.days <= 1) {
            holder.moneyView.text = "+1"
            value = 1
        } else if (period.days <= 3) {
            holder.moneyView.text = "+5"
            value = 5
        } else {
            holder.moneyView.text = "+10"
            value = 10
        }

        // Priority
        holder.nameView.text = item.priority.toString() + ". " + item.name

        // Date
        var formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        var formattedDate = item.date.format(formatter)
        holder.dateView.text = formattedDate

        // Time
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

        // Comment
        holder.commentView.text = item.comment
    }

    fun removeItem(position: Int) {
        values.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val xButton: ImageView = binding.xButton
        val moneyView: TextView = binding.coinText
        val nameView: TextView = binding.todoName
        val dateView: TextView = binding.todoDate
        val timeView: TextView = binding.todoTime
        val commentView: TextView = binding.todoComment
    }

}