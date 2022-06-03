package com.example.classify

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classify.ScheduleActivity.Companion.TODO_LIST
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

lateinit var SCHEDULEACTIVITY: ScheduleActivity

class MyDatabaseManager(context: Context): SQLiteOpenHelper(context, "MyDB",null, 1) {
    // If there's no database already, create one
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS TODOS(" +
                    "PRIORITY INTEGER PRIMARY KEY, " +
                    "DATE TEXT, " +
                    "HOUR INTEGER, " +
                    "MINUTE INTEGER, " +
                    "NAME TEXT, " +
                    "COMMENT TEXT)")
    }

    fun clearDatabase() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM TODOS")
        db.close()
        Log.d("database", "table cleared")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun onDatabaseUpdate(priority: Int) {
        val db = this.writableDatabase
        db.execSQL("UPDATE TODOS SET PRIORITY = PRIORITY + 1 WHERE PRIORITY >= ${priority} ")
        db.close()
        Log.d("database", "todo priorities updated")
    }

    // Delete TodoData info from the database using priority since it's the unique primary key
    fun onDelete(priority: Int) {
        val db = this.writableDatabase
        db.delete("TODOS", "PRIORITY" + "=" + priority, null)
        db.close()
        Log.d("schedule activity", "todo deleted with priority: $priority")
    }

    // Insert SINGLE TodoData into the database
    fun insert(todo: ToDoData) {
        // onCreate() returns a ref to writableDatabase
        // We need to translate this: INSERT INTO CHUCK VALUES("foobar")
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("PRIORITY", todo.priority)
        values.put("DATE", todo.date.toString())
        values.put("HOUR", todo.hour)
        values.put("MINUTE", todo.minute)
        values.put("NAME", todo.name)
        values.put("COMMENT", todo.comment)

        val success = db.insert("TODOS", null, values)
        Log.d("database", "todo inserted: ${todo.toString()}")

        db.close()
    }

    // Insert ALL TodoData into the database
    fun insertAll() {
        val db = this.writableDatabase

        val values = ContentValues()
        for (todo in TODO_LIST) {
            insert(todo)
        }

        db.close()
    }

    // Read all rows from the database and return a list of TodoData items
    fun readAllRows(): List<ToDoData> {
        val result = arrayListOf<ToDoData>()

        // read from database
        // cursor points to table of Query results
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM TODOS", null)

        // iterate over table of Query results and add the new TodoData to result list
        while (cursor.moveToNext()) {
            val priority = cursor.getInt(0)
            val dateStr = cursor.getString(1)
            val date = LocalDate.parse(dateStr)
            val hour = cursor.getInt(2)
            val min = cursor.getInt(3)
            val name = cursor.getString(4)
            val comment = cursor.getString(5)

            val todoItem = ToDoData(date, hour, min, name, comment, priority)
            Log.d("database", "todo read and converted to: ${todoItem.toString()}")
            result.add(todoItem)
        }
        cursor.close()

        val c = db.rawQuery("SELECT COUNT(*) AS TODO_COUNT FROM TODOS", null)
        while (c.moveToNext()) {
            val count = c.getInt(0)
            Log.d("database", "TODOS table length: $count")
        }
        Log.d("database", "TODO_LIST length: ${result.size}")
        c.close()
        db.close()
        return result
    }
}


class ScheduleActivity : AppCompatActivity(), TodoListener, EnterTodoListener, BalanceListener {
    lateinit var balanceText: TextView
    lateinit var stepsText: TextView
    lateinit var database: MyDatabaseManager
    lateinit var adapter: MyTodoListRecyclerViewAdapter
    lateinit var recycler: RecyclerView
    lateinit var dialfrag: EnterTodoDialogFragment
    lateinit var fab: FloatingActionButton


    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST = arrayListOf<ToDoData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        SCHEDULEACTIVITY = this

        balanceText = findViewById(R.id.balance_text)
        stepsText = findViewById(R.id.steps_text)

        database = MyDatabaseManager(this)
        val allRows = database.readAllRows()
        TODO_LIST = ArrayList(allRows)

        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.todoListener = this
        adapter.balanceListenerSchedule = this
//        adapter.balanceListenerMain = MAINACTIVITY
//        adapter.balanceListenerPetCare = PETCARE_ACTIVITY

        recycler = findViewById(R.id.TodoRecyler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        Log.d("schedule activity", "inflated the recycler")

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            Log.d("schedule activity", "fab clicked!")

            dialfrag = EnterTodoDialogFragment.newInstance(this, TODO_LIST.size)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.TodoDialogFrag, dialfrag, "enter todo dialog frag")
                commit()
                Log.d("schedule activity", "inflated enter todo dialog")
            }
        }
    }

    override fun onAddBalance(value: Int) {
        balance += value
        balanceText.text = "$${balance}"
        MAINACTIVITY.updateBalance(balance)
        Log.d("todo removed", "balance = $balance")
    }

    override fun onTodoClick(position: Int) {
        // The onClick implementation of the RecyclerView item click
    }

    override fun onTodoRemove(priority: Int) {
        for (todo in TODO_LIST) {
            if (todo.priority == priority){
                TODO_LIST.remove(todo)
            }
        }
        updatePrioritiesOnDelete(priority)
        insertionSort()

        Log.d("todo removed", "db updated, todo was removed")
        printList()

        runOnUiThread {
            // Then tell adapter that data has changed
            adapter.notifyDataSetChanged()
        }
    }

    override fun todoEntered(
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
        insertionSort()

        Log.d("schedule activity", "db updated, todo was inserted")
        printList()

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
    }

    private fun updatePrioritiesOnInsert(priority: Int) {
        for (todo in TODO_LIST) {
            if (todo.priority >= priority) {
                todo.priority += 1
            }
        }
    }

    private fun insertionSort() {
        if (TODO_LIST.size >= 2) {

            for (count in 1..TODO_LIST.size-1){
                val item = TODO_LIST[count].priority
                var i = count

                while (i > 0 && item < TODO_LIST[i - 1].priority){
                    TODO_LIST[i].priority = TODO_LIST[i - 1].priority
                    i -= 1
                }
                TODO_LIST[i].priority = item
            }
        }
    }

    private fun printList() {
        for (todo in TODO_LIST) {
            Log.d("TODO_LIST contents", " ${todo.priority}. ${todo.toString()}")
        }
    }

    override fun onStop() {
        for (todo in TODO_LIST) {
            database.insert(todo)
        }

        printList()
        super.onStop()
    }

    override fun onDestroy() {
        for (todo in TODO_LIST) {
            database.insert(todo)
        }

        printList()
        super.onDestroy()
    }
}