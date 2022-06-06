package com.example.classify

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.classify.ScheduleActivity.Companion.TODO_LIST
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

    fun clearDatabase() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM TODOS")
        db.close()
        Log.d("database", "table cleared")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    // Insert SINGLE TodoData into the database
    private fun insert(todo: ToDoData) {
        // onCreate() returns a ref to writableDatabase
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("PRIORITY", todo.priority)
        values.put("DATE", todo.date.toString())
        values.put("HOUR", todo.hour)
        values.put("MINUTE", todo.minute)
        values.put("NAME", todo.name)
        values.put("COMMENT", todo.comment)

        db.insert("TODOS", null, values)
        Log.d("database", "todo inserted: $todo")

        db.close()
    }

    // Insert ALL TodoData into the database
    fun insertAll() {
        val db = this.writableDatabase
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
            Log.d("database", "todo read and converted to: $todoItem")
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