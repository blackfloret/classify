package com.example.classify

class ToDoData
    (val date: java.time.LocalDate,
     val hour: Int,
     val minute: Int,
     val name: String,
     val comment: String,
     val priority: Int
    ) {

        override fun toString(): String {
            val str = "$date, $hour:$minute, $name, $comment, $priority"
            return str
        }

}