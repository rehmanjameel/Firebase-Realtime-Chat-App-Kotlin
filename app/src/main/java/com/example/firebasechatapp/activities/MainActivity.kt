package com.example.firebasechatapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.example.firebasechatapp.AppGlobals
import com.example.firebasechatapp.DownloadImageInternal
import com.example.firebasechatapp.R
import com.example.firebasechatapp.adapters.ChatAdapter
import com.example.firebasechatapp.models.Message
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

@SuppressLint("RestrictedApi")

class MainActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val user = auth.currentUser
    private val appGlobals = AppGlobals()
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_signIn_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            appGlobals.saveString("ImageUrl", photoUrl.toString())
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            Log.e("Google", "$name, \n $email \n $photoUrl")
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
//            DownloadImageInternal(this).execute(photoUrl.toString())
//            var file = File(it.filesDir, "Images")
//            if (!file.exists()) {
//                file.mkdir()
//            }
//            file = File(file, "img.jpg")
        }


        initFirebase()

        setUpSendButton()

        createFireBaseListener()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.logoutId) {
            Firebase.auth.signOut()
//            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent= Intent(this, NameActivity::class.java)
                startActivity(intent)
                finish()
//            }
        } else if (id == R.id.removeValuesId) {
            val paths = databaseReference?.path

            Log.e("paths", "$paths")
            databaseReference?.child("$paths")?.removeValue()
            Log.e("paths", "Removed")
        }
        return super.onOptionsItemSelected(item)
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
                    Log.e("message", "$message")
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
//        sharedPreferences = getSharedPreferences("nameKey", Context.MODE_PRIVATE)
//        val getName = sharedPreferences.getString(myName, "")
//        Log.d("UserName: ", getName.toString())

        val uid = appGlobals.getValueString("UID") ?: ""
        val userName = appGlobals.getValueString("userName")
        val profileImageUrl = appGlobals.getValueString("ProfileImageUrl")
        Log.d("UserName", userName.toString())

        databaseReference?.
        child("messages")?.
        child(java.lang.String.valueOf(System.currentTimeMillis()))?.
        setValue(Message(uid, mainEditTextId.text.toString(), userName.toString(), profileImageUrl.toString()))
//        setValue(Message(mainEditTextId.text.toString(), user?.displayName.toString(), user?.photoUrl.toString()))
        mainEditTextId.setText("")
    }
}