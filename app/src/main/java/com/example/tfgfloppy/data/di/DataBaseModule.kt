package com.example.tfgfloppy.data.di

import android.content.Context
import androidx.room.Room
import com.example.tfgfloppy.data.ToDoDatabase
import com.example.tfgfloppy.data.note.NoteDAO
import com.example.tfgfloppy.data.task.TaskDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    fun provideTaskDao(toDoDatabase: ToDoDatabase): TaskDAO {
        return toDoDatabase.taskDao()
    }

    @Provides
    fun provideNoteDao(toDoDatabase: ToDoDatabase): NoteDAO {
        return toDoDatabase.noteDao()
    }

    @Provides
    @Singleton
    fun provideToDoDataBase(@ApplicationContext appContext: Context): ToDoDatabase {
        return Room.databaseBuilder(appContext, ToDoDatabase::class.java, "ToDoDatabase").build()
    }
}