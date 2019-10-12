package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

object RepositoryModule {
    fun createRepositoryModule(context: Context): Module {
        return module {
            factory {
                ServiceLocator.provideTasksRepository(context)
            }
        }
    }
}