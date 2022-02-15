package com.example.firebasechatapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentProviderClient
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_name.*
import java.lang.Exception

class NameActivity : AppCompatActivity() {

    //val databaseReference: DatabaseReference? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val reqCode = 123
    var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("nameKey", MODE_PRIVATE)
//        setUpNameSendButton()

        // Configure Google Sign In inside onCreate method
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_signIn_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // initialize the firebaseAuth variable
        firebaseAuth = FirebaseAuth.getInstance()

        signInId.setOnClickListener {
            signInGoogle()
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intentData : Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
            handleResult(task)
            Log.e("Here", "result Launcher")
        }
    }

    private  fun signInGoogle(){

        val signInIntent:Intent=mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent,reqCode)
        resultLauncher.launch(signInIntent)

        Log.e("Here", "sign in")

    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            Log.d("Auth id", account?.id.toString())

            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        } catch (e:ApiException){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.e("Here", "auth")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
//                    val user = firebaseAuth.currentUser
                    SavedPreference.setEmail(this, account.email.toString())
                    SavedPreference.setUsername(this, account.displayName.toString())

                    val intent = Intent(this, MainActivity::class.java)
//                    resultLauncher.launch(intent)
                    startActivity(intent)
                    finish()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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