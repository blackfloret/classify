package com.example.classify

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

lateinit var SCHEDULE_ACTIVITY: ScheduleActivity

class ScheduleActivity : AppCompatActivity(), TodoListener, EnterTodoListener {
    private lateinit var moneyStepsFragment: MoneyStepsFragment
    private lateinit var database: MyDatabaseManager
    private lateinit var adapter: MyTodoListRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var dialfrag: EnterTodoDialogFragment
    private lateinit var fab: FloatingActionButton
    private var prevTotalSteps = 0f
    private var fabIsVisible: Boolean = true


    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST = arrayListOf<ToDoData>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        SCHEDULE_ACTIVITY = this

        moneyStepsFragment = MoneyStepsFragment()
        supportFragmentManager.beginTransaction().apply {
            add(R.id.moneyStepsFragmentContainerView, moneyStepsFragment, "moneySteps")
            commit()
        }

        updateBalance(sf.getInt("balance", 0))

        moneyStepsFragment.updateValues()

        database = MyDatabaseManager(this)
        val allRows = database.readAllRows()
        TODO_LIST = ArrayList(allRows)
        Log.d("TODO_LIST","Initial onRead()")
        printList()

        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.todoListener = this

        recycler = findViewById(R.id.TodoRecyler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        Log.d("schedule activity", "inflated the recycler")

        fabIsVisible = true
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            Log.d("schedule activity", "fab clicked!")

            dialfrag = EnterTodoDialogFragment.newInstance(this, TODO_LIST.size)
            dialfrag.show(supportFragmentManager, "inflated enter todo frag")
//            supportFragmentManager.beginTransaction().apply {
//                fabIsVisible = false
//                fab.isVisible = false
//                replace(R.id.TodoDialogFrag, dialfrag, "enter todo dialog frag")
//                commit()
//                Log.d("schedule activity", "inflated enter todo dialog")
//            }
        }

        savedInstanceState?.let {
            fabIsVisible = it.getBoolean("fab") ?: false
        }

        if (fabIsVisible) {
            fab.isVisible = true
        } else {
            fab.isVisible = false
        }
    }

    // Preserve original values of balance and steps on changing activities
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("fab", fabIsVisible)
        super.onSaveInstanceState(outState)
        with(sf.edit()) {
            putInt("balance", balance)
            putFloat("prevSteps", prevTotalSteps)
            apply()
        }
    }

    private fun updateBalance(newBalance: Int) {
        balance = newBalance
        moneyStepsFragment.updateValues()
        with(sf.edit()) {
            putInt("balance", balance)
            apply()
        }
    }

    override fun onTodoClick(position: Int) {
        // The onClick implementation of the RecyclerView item click
    }

    override fun onTodoRemove(value: Int, priority: Int) {
        updateBalance(balance+value)
        for (todo in TODO_LIST) {
            if (todo.priority == priority){
                TODO_LIST.remove(todo)
            }
        }

        if (TODO_LIST.size >= 2) {
            updatePrioritiesOnDelete(priority)
            insertionSort()
        }

        Log.d("TODO_LIST", "todo w. priority = $priority was removed from TODO_LIST")
        printList()

        runOnUiThread {
            // Then tell adapter that data has
            adapter.notifyDataSetChanged()
            Log.d("TODO_LIST", "del todo, thus update recycler view")
            printList()
        }
    }

    override fun onTodoEntered(
        localDate: LocalDate,
        hour: Int,
        minute: Int,
        name: String,
        comment: String,
        priority: Int
    ) {
        val newData = ToDoData(localDate, hour, minute, name, comment, priority)
        updatePrioritiesOnInsert(newData.priority)
        TODO_LIST.add(newData)
        Log.d("TODO_LIST", "before insertionSort()")
        printList()
        insertionSort()

        Log.d("TODO_LIST", "after insertionSort()")
        printList()

        fab.isVisible = true
        supportFragmentManager.beginTransaction().remove(dialfrag).commit()

        runOnUiThread {
            // Then tell adapter that data has changed
            adapter.notifyDataSetChanged()
        }
    }

    private fun updatePrioritiesOnDelete(priority: Int) {
        for (todo in TODO_LIST) {
            if (todo.priority >= priority) {
                todo.priority -= 1
            }
        }
        Log.d("TODO_LIST", "updated priorities after delete")
        printList()
    }

    private fun updatePrioritiesOnInsert(priority: Int) {
        for (todo in TODO_LIST) {
            if (todo.priority >= priority) {
                todo.priority += 1
            }
        }
        Log.d("TODO_LIST", "updated priorities after insertion")
        printList()
    }

    // Sort todoData based on ascending priority
    private fun insertionSort() {
        val lastIndex: Int = TODO_LIST.size - 1

        for (count in 1..lastIndex){
            val cur = TODO_LIST[count]
            val curPriority = TODO_LIST[count].priority
            var i = count
            while ((i > 0) && (curPriority < TODO_LIST[i - 1].priority)){
                TODO_LIST[i] = TODO_LIST[i - 1]
                i -= 1
            }
            TODO_LIST[i] = cur
        }
    }

    // Log TODO_LIST contents
    private fun printList() {
        for (count in 0..TODO_LIST.size-1) {
            Log.d("TODO_LIST contents", "Index $count: ${TODO_LIST[count].toString()}")
        }
    }

    // Add all TodoData to database whenever we switch off Schedule Activity or the app
    //      Note: This is the only time we add TodoData to the database
    override fun onStop() {
        database.clearDatabase()
        database.insertAll()
        Log.d("schedule activity", "ON STOP()")
        printList()
        super.onStop()
    }

    override fun onDestroy() {
        database.clearDatabase()
        database.insertAll()
        Log.d("schedule activity", "ON STOP()")
        printList()
        super.onDestroy()
    }
}