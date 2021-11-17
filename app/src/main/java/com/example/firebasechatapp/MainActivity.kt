package com.example.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.example.firebasechatapp.adapters.ChatAdapter
import com.example.firebasechatapp.models.Message
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()

        setUpSendButton()

        createFireBaseListener()
    }

    //* Setting up the Firebase **//
    //***
    private fun initFirebase() {

        //initializing firebase
        FirebaseApp.initializeApp(applicationContext)

        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

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
                toReturn.sortBy { message ->
                    message.timeStamp
                }
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
        chatRecyclerView.adapter = ChatAdapter(data) {
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
        databaseReference?.
        child("messages")?.
        child(java.lang.String.valueOf(System.currentTimeMillis()))?.
        setValue(Message(mainEditTextId.text.toString()))

        mainEditTextId.setText("")
    }
}