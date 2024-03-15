package ru.kpfu.itis.gr201.ponomarev.db

import org.jetbrains.exposed.sql.Database

object DatabaseSingleton {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = System.getenv("SPOTIFY_DB_JDBC_URL")
        val user = System.getenv("SPOTIFY_DB_USER")
        val password = System.getenv("SPOTIFY_DB_PASSWORD")
        Database.connect(jdbcUrl, driverClassName, user, password)
    }
}