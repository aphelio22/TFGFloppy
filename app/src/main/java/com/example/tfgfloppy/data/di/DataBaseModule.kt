package com.example.tfgfloppy.data.di

import android.content.Context
import androidx.room.Room
import com.example.tfgfloppy.data.ToDoDatabase
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
    @Singleton
    fun provideToDoDataBase(@ApplicationContext appContext: Context): ToDoDatabase {
        return Room.databaseBuilder(appContext, ToDoDatabase::class.java, "ToDoDatabase").build()
    }
}