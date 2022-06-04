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
import com.example.classify.ScheduleActivity.Companion.TODO_LIST
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

lateinit var SCHEDULE_ACTIVITY: ScheduleActivity

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
        db.execSQL("UPDATE TODOS SET PRIORITY = PRIORITY + 1 WHERE PRIORITY >= $priority ")
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
    private fun insert(todo: ToDoData) {
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


class ScheduleActivity : AppCompatActivity(), TodoListener, EnterTodoListener {
    private lateinit var moneyStepsFragment: MoneyStepsFragment
    private lateinit var database: MyDatabaseManager
    private lateinit var adapter: MyTodoListRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var dialfrag: EnterTodoDialogFragment
    private lateinit var fab: FloatingActionButton
    private var prevTotalSteps = 0f


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

        sf = getPreferences(Context.MODE_PRIVATE)
        updateBalance(sf.getInt("balance", 0))
        prevTotalSteps = sf.getFloat("prevSteps", 0f)

        moneyStepsFragment.updateValues()

        database = MyDatabaseManager(this)
        val allRows = database.readAllRows()
        TODO_LIST = ArrayList(allRows)
        Log.d("TODO_LIST","Initial onRead()")
        printList()

        adapter = MyTodoListRecyclerViewAdapter(TODO_LIST)
        adapter.todoListener = this
//        adapter.balanceListenerSchedule = this
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
                fab.isVisible = false
                replace(R.id.TodoDialogFrag, dialfrag, "enter todo dialog frag")
                commit()
                Log.d("schedule activity", "inflated enter todo dialog")
            }
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
            // Then tell adapter that data has changed
            adapter.notifyDataSetChanged()
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
                Log.d("TODO_LIST", "insertionSort() iteration count: $count, i: $i")
                printList()
            }
            TODO_LIST[i] = cur
            Log.d("TODO_LIST", "insertionSort() fin")
            printList()
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
        database.insertAll()
        Log.d("schedule activity", "ON STOP()")
        printList()
        super.onStop()
    }
}