package com.example.timerapp

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import java.lang.NumberFormatException

private const val TAG = "ToDoList"
private const val CONTENT = "Text_Content"
class ToDoList : AppCompatActivity() {
    var count: Long = 0
    private var taskList: TextView?=null
    val dbHelper = DatabaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        val database = baseContext.openOrCreateDatabase("sqlite-test-1.db", Context.MODE_PRIVATE, null)
        //var sql = "CREATE TABLE IF NOT EXISTS tasks(_id INTEGER PRIMARY KEY NOT NULL, description TEXT)"
        //database.execSQL(sql)

        val editText: EditText =findViewById<EditText>(R.id.editText)
        val buttonSubmit: Button =findViewById<Button>(R.id.buttonSubmit)
        val buttonClear: Button =findViewById<Button>(R.id.buttonCancel)
        editText.text.clear()
        taskList=findViewById<TextView>(R.id.taskList)

        var dataID: Long = 0;
        displayTasks()
        buttonSubmit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?){
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put("description", editText.text.toString())
                }
                dataID = db.insert("tasks", null, values)
                Log.d(TAG, editText.text.toString())
                editText.text.clear()
                displayTasks()
            }
        })
        buttonClear.setOnClickListener(object: View.OnClickListener{
            override fun onClick(view: View){
                val db = dbHelper.writableDatabase
                count=0;
                try{
                    count=editText.text.toString().toLong()
                } catch (e: NumberFormatException){
                    Log.d(TAG, "number format exception")
                }
                editText.text.clear()
                db.delete("tasks", "_ID"+"="+count, null);
                displayTasks()
            }
        })
    }

    fun displayTasks() {
        taskList?.text=""
        val db = dbHelper.readableDatabase
        val projection = arrayOf("_ID", "Description")
        val sortOrder = "_ID"
        val cursor = db.rawQuery("select * from tasks",null);
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow("_ID"))
                val desc = getString(getColumnIndexOrThrow("Description"))
                taskList?.append("$itemId. ")
                taskList?.append(desc)
                taskList?.append("\n")
                Log.d(TAG, desc)
            }
        }
        cursor.close()
    }
}