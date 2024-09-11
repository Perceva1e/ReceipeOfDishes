package com.example.recipeofthedishes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "app", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT,
                email TEXT,
                pass TEXT
            )
        """
        db?.execSQL(createUsersTable)

        val createItemsTable = """
            CREATE TABLE items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                image TEXT,
                title TEXT,
                ingredient TEXT,
                description TEXT,
                instructions TEXT,
                FOREIGN KEY(user_id) REFERENCES users(id)
            )
        """
        db?.execSQL(createItemsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS items")
        onCreate(db)
    }

    fun addUser(user: User) {
        val db = this.writableDatabase

        val userValues = ContentValues().apply {
            put("login", user.login)
            put("email", user.email)
            put("pass", user.password)
        }

        val userId = db.insert("users", null, userValues)

        if (userId == -1L) {
            Log.d("DbHelper", "Error adding user")
        } else {
            Log.d("DbHelper", "User added successfully")

            user.items.forEach { item ->
                val itemValues = ContentValues().apply {
                    put("user_id", userId)
                    put("image", item.image)
                    put("title", item.title)
                    put("ingredient", item.ingredient)
                    put("description", item.desc)
                    put("instructions", item.instructions)
                }
                db.insert("items", null, itemValues)
            }
        }
        db.close()
    }

    fun getUser(login: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM users WHERE login = ? AND pass = ?",
            arrayOf(login, pass)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getUserId(login: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id FROM users WHERE login = ? AND pass = ?",
            arrayOf(login, pass)
        )
        val userId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        } else {
            -1
        }
        cursor.close()
        return userId
    }

    fun deleteItem(itemId: Int) {
        val db = this.writableDatabase
        db.delete("items", "id = ?", arrayOf(itemId.toString()))
        db.close()
    }

    fun getUserItems(userId: Int): List<Item> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM items WHERE user_id = ?",
            arrayOf(userId.toString())
        )

        val items = mutableListOf<Item>()
        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    image = cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    ingredient = cursor.getString(cursor.getColumnIndexOrThrow("ingredient")),
                    desc = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"))                )
                items.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun addItem(userId: Int, item: Item) {
        val db = this.writableDatabase
        val itemValues = ContentValues().apply {
            put("user_id", userId)
            put("image", item.image)
            put("title", item.title)
            put("ingredient", item.ingredient)
            put("description", item.desc)
            put("instructions", item.instructions)
        }
        db.insert("items", null, itemValues)
        db.close()
    }
}
