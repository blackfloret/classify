package com.example.classify

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

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

    fun onDelete() {
//        val db = this.writableDatabase
//        db?.execSQL("DELETE  FROM TODOS");
    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    // Insert TodoData info into the database
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
        Log.d("schedule activity", "success: $success")
        Log.d("schedule activity", "todo to be inserted: ${todo.toString()}")

        db.close()
    }

    // Read all rows from the database and return a list of strings
    fun readAllRows(): List<ToDoData> {
        val result = mutableListOf<ToDoData>()

        // read from database
        // cursor points to table of Query results
        val cursor = writableDatabase.rawQuery("SELECT * FROM TODOS", null)

        // iterate over table of Query results and add the todo to result variable
        while (cursor.moveToNext()) {
            val priority = cursor.getInt(0)
            val dateStr = cursor.getString(1)
            val date = LocalDate.parse(dateStr)
            val hour = cursor.getInt(2)
            val min = cursor.getInt(3)
            val name = cursor.getString(4)
            val comment = cursor.getString(5)

            val str = "$dateStr, $hour:$min, $name, $comment, $priority"
            Log.d("schedule activity", "todo read: $str")
            val todoItem = ToDoData(date, hour, min, name, comment, priority)
            Log.d("schedule activity", "todo read and converted to: ${todoItem.toString()}")
            result.add(todoItem)
        }

        cursor.close()

        val c = writableDatabase.rawQuery("SELECT COUNT(*) AS TODO_COUNT FROM TODOS", null)
        while (c.moveToNext()) {
            val count = c.getInt(0)
            Log.d("schedule activity", "todo_list table length: $count")
        }
        Log.d("schedule activity", "todo_list length: ${result.size}")
        c.close()
        return result
    }
}


class ScheduleActivity : AppCompatActivity(), OnTodoClicked, EnterTodoListener {
    lateinit var recycler: RecyclerView
    lateinit var adapter: MyTodoListRecyclerViewAdapter
    lateinit var dialfrag: EnterTodoDialogFragment
    lateinit var fab: FloatingActionButton
    lateinit var database: MyDatabaseManager

    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST: MutableList<ToDoData> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        database = MyDatabaseManager(this)
        val allRows = database.readAllRows()
        TODO_LIST = allRows.toMutableList()

        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.onClick = this

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

                recycler.setAdapter(null)
                fab.visibility = INVISIBLE
                Log.d("schedule activity", "enter todo inflated")
            }
        }
    }

    override fun onTodoClick(position: Int) {
        // The onClick implementation of the RecyclerView item click
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

        database.insert(newData)
        val allRows = database.readAllRows()
        TODO_LIST = allRows.toMutableList()
        Log.d("schedule activity", "db updated")

        supportFragmentManager.beginTransaction().remove(dialfrag).commit()
        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        recycler.setAdapter(adapter)
        fab.visibility = VISIBLE

        runOnUiThread {
            // Then tell adapter that data has changed
            adapter.notifyDataSetChanged()
        }
    }
}