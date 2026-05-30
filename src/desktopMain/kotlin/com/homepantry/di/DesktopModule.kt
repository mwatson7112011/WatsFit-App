package com.homepantry.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single { 
        val dbDir = File(System.getProperty("user.home"), ".homepantry")
        if (!dbDir.exists()) dbDir.mkdirs()

        val dbPath = File(dbDir, "pantry_v2.db").absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

        try {
            PantryDatabase.Schema.create(driver)
        } catch (_: Exception) {
            // Migration could go here, but for now we'll just ensure it exists
        }

        PantryDatabase(driver)
    }
}
