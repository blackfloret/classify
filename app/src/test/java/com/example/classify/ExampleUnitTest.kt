package com.example.classify

import android.util.Log
import com.example.classify.ScheduleActivity.Companion.TODO_LIST
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun todoAdded() {
        Whitebox.setInternalState(ScheduleActivity.class, TODO_LIST)
        TODO_LIST = arrayListOf<ToDoData>()

        ScheduleActivity().onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        ScheduleActivity().onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        ScheduleActivity().onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)

        for (todo in TODO_LIST) {
            System.out.println("unit test: " + todo.toString())
        }

        assertEquals(ToDoData(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1),
            TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2),
            TODO_LIST[1]
        )

        assertEquals(ToDoData(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3),
            TODO_LIST[2]
        )
    }

    @Test
    fun todoRemoved() {
        TODO_LIST = arrayListOf<ToDoData>()

        TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)

        ScheduleActivity().onTodoRemove(1, 2)
        assertEquals(ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1),
            TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2030, 12, 30), 3, 3,"", "", 2),
            TODO_LIST[1]
        )
    }

    @Test
    fun todoPriorityChangedOnInsert() {
        TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)
        TODO_LIST[3] = ToDoData(LocalDate.of(2040, 12, 30), 4, 4,"", "", 4)
        ScheduleActivity().onTodoEntered(LocalDate.of(2050, 12, 30), 5, 5,"", "", 2)


        assertEquals(ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1),
            TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2050, 12, 30), 5, 5,"", "", 2),
            TODO_LIST[1]
        )

        assertEquals(ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 3),
            TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 4),
            TODO_LIST[0]
        )

        assertEquals(ToDoData(LocalDate.of(2040, 12, 30), 4, 4,"", "", 5),
            TODO_LIST[0]
        )

        assertEquals(1, TODO_LIST[0].priority)
        assertEquals(2, TODO_LIST[1].priority)
        assertEquals(3, TODO_LIST[2].priority)
        assertEquals(4, TODO_LIST[3].priority)
        assertEquals(5, TODO_LIST[4].priority)
    }

    @Test
    fun TODO_LIST_size_increase(){
        TODO_LIST = arrayListOf<ToDoData>()

        ScheduleActivity().onTodoEntered(LocalDate.of(2010, 12, 30), 1, 1,"", "", 1)
        ScheduleActivity().onTodoEntered(LocalDate.of(2020, 12, 30), 2, 2,"", "", 2)
        ScheduleActivity().onTodoEntered(LocalDate.of(2030, 12, 30), 3, 3,"", "", 3)
        ScheduleActivity().onTodoEntered(LocalDate.of(2040, 12, 30), 4, 4,"", "", 4)

        assertEquals(4, TODO_LIST.size)
    }

    @Test
    fun TODO_LIST_size_decrease(){
        TODO_LIST = arrayListOf<ToDoData>()
        TODO_LIST[0] = ToDoData(LocalDate.of(2000, 12, 30), 23, 59,"", "", 1)
        TODO_LIST[1] = ToDoData(LocalDate.of(2010, 12, 30), 23, 59,"", "", 2)
        TODO_LIST[2] = ToDoData(LocalDate.of(2020, 12, 30), 23, 59,"", "", 3)

        ScheduleActivity().onTodoRemove(1, 3)

        assertEquals(2, TODO_LIST.size)
    }

}