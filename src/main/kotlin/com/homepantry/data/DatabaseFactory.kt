package com.homepantry.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.homepantry.db.PantryDatabase
import java.io.File

object DatabaseFactory {
    private var database: PantryDatabase? = null

    fun create(): PantryDatabase {
        if (database != null) return database!!

        val dbDir = File(System.getProperty("user.home"), ".homepantry")
        if (!dbDir.exists()) dbDir.mkdirs()

        val dbPath = File(dbDir, "pantry.db").absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

        // Create tables if they don't exist
        try {
            PantryDatabase.Schema.create(driver)
        } catch (_: Exception) {
            // Tables already exist — this is fine
        }

        database = PantryDatabase(driver)
        return database!!
    }
}
