package com.app.test.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Sweven on 2025/1/6--9:13.
 * Email: sweventears@163.com
 */
class SQLConnect private constructor(context: Context) {
    companion object {
        // 数据库名
        private const val DB_NAME = "test.db"

        // 数据库版本
        private const val DB_VERSION = 1

        // 单例模式
        @Volatile
        private var instance: SQLConnect? = null

        fun getInstance(context: Context): SQLConnect {
            return instance?: synchronized(this) {
                instance?: SQLConnect(context).also { instance = it }
            }
        }
    }
    private var dbHelper: SQLiteOpenHelper
    private var db: SQLiteDatabase

    init {
        // 获取 SQLiteOpenHelper 实例
        dbHelper = object : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            override fun onCreate(db: SQLiteDatabase?) {
                // 创建表格
                db?.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "age INTEGER NOT NULL)")
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                // 数据库升级
                db?.execSQL("DROP TABLE IF EXISTS users")
                onCreate(db)
            }
        }


        // 获取 SQLiteDatabase 实例
        db = dbHelper.writableDatabase

        // 执行 SQL 语句
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "age INTEGER NOT NULL)")

        // ��入数据
        val values = ContentValues()
        values.put("name", "John Doe")
        values.put("age", 25)
        db.insert("users", null, values)

        // 查询数据
        val cursor = db.query("users", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val age = cursor.getInt(cursor.getColumnIndex("age"))
            println("ID: $id, Name: $name, Age: $age")
        }
        cursor.close()

        // 关闭数据库
        db.close()

        // 销�� SQLiteOpenHelper 实例
        dbHelper.close()

        // 销�� Context
        context.deleteDatabase(DB_NAME)
    }

    fun getDb(): SQLiteDatabase {
        db = dbHelper.writableDatabase
        return db
    }
}