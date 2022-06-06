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

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var scheduleActivity: ScheduleActivity

    @Before
    fun setup() {
        scheduleActivity = mock()
        val list = scheduleActivity.TODO_LIST


        whenever(scheduleActivity.onTodoEntered(localDate, hour, minute, name, comment, priority))
            .thenAnswer {
                list.add(ToDoData(localDate, hour, minute, name, comment, priority))
            }
    }

    @Test
    fun todoAdded() {
        scheduleActivity.onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        scheduleActivity.onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        scheduleActivity.onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)

        assertEquals(ToDoData(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2),
            scheduleActivity.TODO_LIST[1]
        )

        assertEquals(ToDoData(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3),
            scheduleActivity.TODO_LIST[2]
        )
    }

    @Test
    fun todoRemoved() {
        scheduleActivity.TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        scheduleActivity.TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        scheduleActivity.TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)

        scheduleActivity.onTodoRemove(1, 2)
        assertEquals(ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2030, 12, 30), 3, 3,"", "", 2),
            scheduleActivity.TODO_LIST[1]
        )
    }

    @Test
    fun todoPriorityChangedOnInsert() {
        scheduleActivity.TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        scheduleActivity.TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        scheduleActivity.TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)
        scheduleActivity.TODO_LIST[3] = ToDoData(LocalDate.of(2040, 12, 30), 4, 4,"", "", 4)
        scheduleActivity.onTodoEntered(LocalDate.of(2050, 12, 30), 5, 5,"", "", 2)


        assertEquals(ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2050, 12, 30), 5, 5,"", "", 2),
            scheduleActivity.TODO_LIST[1]
        )

        assertEquals(ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 3),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 4),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2040, 12, 30), 4, 4,"", "", 5),
            scheduleActivity.TODO_LIST[0]
        )

        assertEquals(1, scheduleActivity.TODO_LIST[0].priority)
        assertEquals(2, scheduleActivity.TODO_LIST[1].priority)
        assertEquals(3, scheduleActivity.TODO_LIST[2].priority)
        assertEquals(4, scheduleActivity.TODO_LIST[3].priority)
        assertEquals(5, scheduleActivity.TODO_LIST[4].priority)
    }

    @Test
    fun TODO_LIST_size_increase(){
        scheduleActivity.TODO_LIST = arrayListOf<ToDoData>()

        scheduleActivity.onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        scheduleActivity.onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        scheduleActivity.onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)
        scheduleActivity.onTodoEntered(LocalDate.of(2040, 12, 30), 4, 4,"", "", 4)

        assertEquals(4, scheduleActivity.TODO_LIST.size)
    }

    @Test
    fun TODO_LIST_size_decrease(){
        scheduleActivity.TODO_LIST = arrayListOf<ToDoData>()
        scheduleActivity.TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        scheduleActivity.TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        scheduleActivity.TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)

        scheduleActivity.onTodoRemove(1, 3)

        assertEquals(2, scheduleActivity.TODO_LIST.size)
    }

}