package com.example.tfgfloppy.data

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tfgfloppy.data.note.NoteDAO
import com.example.tfgfloppy.data.note.NoteEntity
import com.example.tfgfloppy.data.task.TaskDAO
import com.example.tfgfloppy.data.task.TaskEntity

@Database(entities = [TaskEntity::class, NoteEntity::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDAO
    abstract fun noteDao(): NoteDAO

}