package com.example.firebasechatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : AppCompatActivity() {

    //val databaseReference: DatabaseReference? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        sharedPreferences = getSharedPreferences("nameKey", MODE_PRIVATE)
        setUpNameSendButton()

    }

    private fun setUpNameSendButton() {
        registerNameButton.setOnClickListener {
            if (editTextId.text.toString() != "") {
                sendName()
                editTextId.setText("")
            }else {
                editTextId.error = "Name required"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun sendName() {
        val name = editTextId.text.toString().trim()

        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("Name", name)
        sharedPreferencesEditor.apply()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("Name", name)
        }
        startActivity(intent)
        finish()
    }
}