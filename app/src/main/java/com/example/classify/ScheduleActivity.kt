package com.example.classify

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

// Canvas API Reference: https://canvas.instructure.com/doc/api/index.html

class MyDatabaseManager(context: Context): SQLiteOpenHelper(context, "MyDB",null, 1) {
    // If there's no database already, create one
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS TODOS(NAME, DATE, HOUR, MINUTE, COMMENT, PRIORITY)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    // Insert TodoData info into the database
    fun insert(todo: ToDoData) {
        // onCreate() returns a ref to writableDatabase
        // We need to translate this: INSERT INTO CHUCK VALUES("foobar")
        writableDatabase.execSQL("INSERT INTO TODOS NAME(\"${todo.name}\")")
        writableDatabase.execSQL("INSERT INTO TODOS DATE(\"${todo.date}\")")
        writableDatabase.execSQL("INSERT INTO TODOS HOUR(\"${todo.hour}\")")
        writableDatabase.execSQL("INSERT INTO TODOS MINUTE(\"${todo.minute}\")")
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
            val name = cursor.getString(0)
            val dateStr = cursor.getString(1)
            val date = LocalDate.parse(dateStr)
            val hour = cursor.getInt(2)
            val min = cursor.getInt(3)
            val comment = cursor.getString(4)
            val priority = cursor.getInt(5)

            val todoItem = ToDoData(name, date, hour, min, comment, priority)
            result.add(todoItem)
        }

        cursor.close()
        return result
    }
}


class ScheduleActivity : AppCompatActivity(), OnItemClicked {
    lateinit var recycler: RecyclerView
    lateinit var adapter: MyTodoListRecyclerViewAdapter
    lateinit var listfrag: TodoListFragment
    // lateinit var dialfrag: EnterTodoFragment

    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST: MutableList<ToDoData> = mutableListOf(
            ToDoData("Task", LocalDate.of(2021, 12,22), 7, 30, "comment here", 1),
            ToDoData("Exam", LocalDate.of(2022, 6,17), 21, 20, "eeeeeeeeeeeeeeeeeeeeeextreeeeeemly loooong text", 2),
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        recycler = findViewById(R.id.TodoFrag)
        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.onClick = this
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        Log.d("schedule activity", "inflated the recycler")

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            Log.d("schedule activity", "fab clicked!")
//            dialfrag = EnterWorkoutDialogFragment.newInstance("hi", "bye")
//            dialfrag.show(supportFragmentManager, "Adding Workout")
//            dialfrag.listener = this
        }
    }

    override fun onItemClick(position: Int) {
        // The onClick implementation of the RecyclerView item click

    }
}