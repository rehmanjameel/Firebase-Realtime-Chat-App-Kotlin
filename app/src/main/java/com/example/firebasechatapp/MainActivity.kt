package com.example.firebasechatapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.example.firebasechatapp.adapters.ChatAdapter
import com.example.firebasechatapp.models.Message
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.prefs.AbstractPreferences
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    var databaseReference: DatabaseReference? = null
    //private lateinit var personName: String
    private val myName = "Name"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Getting name using activity intent
        //personName = intent.getStringExtra("Name").toString()
        //println("UserName: $personName")


        initFirebase()

        setUpSendButton()

        createFireBaseListener()

    }

    //* Setting up the Firebase **//
    //***
    private fun initFirebase() {

        //initializing firebase
        FirebaseApp.initializeApp(applicationContext)

//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

        //Get reference to Firebase DB
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    //** Setting up the Firebase Listener **//
    //**
    private fun createFireBaseListener() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val toReturn: ArrayList<Message> = ArrayList()

                for (data in snapshot.children) {
                    val messageData = data.getValue(Message::class.java)
                    //unwrap
                    val message = messageData ?: continue
                    toReturn.add(message)
                }
                //sort to newest at bottom
                toReturn.sortBy { message: Message ->
                    message.timeStamp
                }
//                toReturn.reverse()
                setAdapter(toReturn)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        databaseReference?.child("messages")?.addValueEventListener(postListener)
    }

    //** Setting adapter to fetch the messages from firebase in recycler view **//
    //***
    private fun setAdapter(data: ArrayList<Message>){

        val linearLayoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = linearLayoutManager
        chatRecyclerView.adapter = ChatAdapter(this, data)
        {
            Toast.makeText(this, "${it.text} clicked", Toast.LENGTH_SHORT).show()
        }
        //scroll to bottom
        chatRecyclerView.scrollToPosition(data.size - 1)
    }

    //** Setting the send Button onClickListener **//
    //***
    private fun setUpSendButton() {
        messageSendButton.setOnClickListener {
            if (mainEditTextId.text.toString().isNotEmpty()) {
                sendData()
            }else{
                mainEditTextId.error = "Text required"
            }
        }
    }

    //** Sending message to firebase RealTime Database **//
    //***
    private fun sendData() {
        //Getting name using the SharedPreferences
        sharedPreferences = getSharedPreferences("nameKey", Context.MODE_PRIVATE)
        val getName = sharedPreferences.getString(myName, "")
        Log.d("UserName: ", getName.toString())

        databaseReference?.
        child("messages")?.
        child(java.lang.String.valueOf(System.currentTimeMillis()))?.
        setValue(Message(mainEditTextId.text.toString(), getName.toString()))
        mainEditTextId.setText("")
    }
}