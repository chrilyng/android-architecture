package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

object RepositoryModule {
    fun createRepositoryModule(context: Context): Module {
        return module {
            single {
                createTaskLocalDataSource(context)
            }

            factory {
                DefaultTasksRepository(TasksRemoteDataSource, get()) as TasksRepository
            }
        }
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        return Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java, "Tasks.db"
        ).addMigrations(ToDoDatabase.MIGRATION_1_2).build()
    }
}