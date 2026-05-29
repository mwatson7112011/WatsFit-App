package com.homepantry.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.homepantry.db.PantryDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { 
        val driver = AndroidSqliteDriver(PantryDatabase.Schema, androidContext(), "pantry.db")
        PantryDatabase(driver)
    }
}
