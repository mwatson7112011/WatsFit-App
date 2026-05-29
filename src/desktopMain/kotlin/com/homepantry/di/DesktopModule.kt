package com.homepantry.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single { 
        val dbDir = File(System.getProperty("user.home"), ".homepantry")
        if (!dbDir.exists()) dbDir.mkdirs()

        val dbPath = File(dbDir, "pantry.db").absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

        try {
            PantryDatabase.Schema.create(driver)
        } catch (_: Exception) { }

        PantryDatabase(driver)
    }
}
