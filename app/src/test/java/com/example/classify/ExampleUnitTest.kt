package com.example.classify

import android.util.Log
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var scheduleActivity: ScheduleActivity
    private lateinit var list: ArrayList<ToDoData>
    @Before
    fun setup() {
        scheduleActivity = mock()
        list = scheduleActivity.TODO_LIST


        whenever(scheduleActivity.onTodoEntered(localDate, hour, minute, name, comment, priority))
            .thenAnswer {
                list.add(ToDoData(localDate, hour, minute, name, comment, priority))
            }
    }

    @Test
    fun todoAdded() {
        list = arrayListOf<ToDoData>()

        scheduleActivity.onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        scheduleActivity.onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        scheduleActivity.onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)
        assertEquals(arrayListOf<ToDoData>(), list)
    }

    @Test
    fun TODO_LIST_size_increase(){
        list = arrayListOf<ToDoData>()

        scheduleActivity.onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        scheduleActivity.onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        scheduleActivity.onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)
        scheduleActivity.onTodoEntered(LocalDate.of(2040, 12, 30), 4, 4,"", "", 4)
        assertEquals(arrayListOf<ToDoData>(), list)
    }
}