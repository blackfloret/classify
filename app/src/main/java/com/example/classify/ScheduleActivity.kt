package com.example.classify

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    // Insert a todo into the database
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


class ScheduleActivity : AppCompatActivity() {
    // Create a list of todos
    // remember that companion objects create static class variables
    companion object {
        var TODO_LIST: MutableList<ToDoData> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)


    }
}