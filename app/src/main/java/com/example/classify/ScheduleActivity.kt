package com.example.classify

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class MyDatabaseManager(context: Context): SQLiteOpenHelper(context, "MyDB",null, 1) {
    // If there's no database already, create one
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS TODOS(DATE, HOUR, MINUTE, NAME, COMMENT, PRIORITY)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    // Insert TodoData info into the database
    fun insert(todo: ToDoData) {
        // onCreate() returns a ref to writableDatabase
        // We need to translate this: INSERT INTO CHUCK VALUES("foobar")
        writableDatabase.execSQL("INSERT INTO TODOS DATE(\"${todo.date}\")")
        writableDatabase.execSQL("INSERT INTO TODOS HOUR(\"${todo.hour}\")")
        writableDatabase.execSQL("INSERT INTO TODOS MINUTE(\"${todo.minute}\")")
        writableDatabase.execSQL("INSERT INTO TODOS NAME(\"${todo.name}\")")
        writableDatabase.execSQL("INSERT INTO TODOS COMMENT(\"${todo.comment}\")")
        writableDatabase.execSQL("INSERT INTO TODOS PRIORITY(\"${todo.priority}\")")
    }

    // Read all rows from the database and return a list of strings
    fun readAllRows(): List<ToDoData> {
        var result = mutableListOf<ToDoData>()

        // read from database
        // cursor points to table of Query results
        val cursor = writableDatabase.rawQuery("SELECT * FROM TODOS", null)

        // iterate over table of Query results and add the joke to result variable
        while (cursor.moveToNext()) {
            val dateStr = cursor.getString(0)
            val date = LocalDate.parse(dateStr)
            val hour = cursor.getInt(1)
            val min = cursor.getInt(2)
            val name = cursor.getString(3)
            val comment = cursor.getString(4)
            val priority = cursor.getInt(5)

            val todoItem = ToDoData(date, hour, min, name, comment, priority)
            result.add(todoItem)
        }

        cursor.close()
        return result
    }
}


class ScheduleActivity : AppCompatActivity(), OnItemClicked, EnterTodoListener {
    lateinit var recycler: RecyclerView
    lateinit var adapter: MyTodoListRecyclerViewAdapter
    lateinit var dialfrag: EnterTodoDialogFragment

    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST: MutableList<ToDoData> = mutableListOf(
            ToDoData(LocalDate.of(2021, 12,22), 7, 30, "Task",  "comment here", 1),
            ToDoData(LocalDate.of(2022, 6,17), 21, 20, "Exam", "eeeeeeeeeeeeeeeeeeeeeextreeeeeemly loooong text", 2)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        //TOOD_LIST = readAllRows()
        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.onClick = this

        recycler = findViewById(R.id.TodoRecyler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        Log.d("schedule activity", "inflated the recycler")

        val fab: FloatingActionButton = findViewById(R.id.fab)
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

    override fun onItemClick(position: Int) {
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
        var newData = ToDoData(localDate, hour, minute, name, comment, priority)
        TODO_LIST.add(newData)
        supportFragmentManager.beginTransaction().remove(dialfrag).commit()
        Log.d("schedule activity", "TOOD_LIST updated")

        runOnUiThread {
            // Then tell adapter that data has changed
            adapter.notifyDataSetChanged()
        }
    }
}