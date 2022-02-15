package com.example.firebasechatapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import com.bumptech.glide.Glide
import com.example.firebasechatapp.AppGlobals
import com.example.firebasechatapp.R
import com.example.firebasechatapp.SavedPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_name.*
import java.util.*
import java.util.jar.Manifest

@SuppressLint("WrongConstant")

class NameActivity : AppCompatActivity() {

    //val databaseReference: DatabaseReference? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var mGoogleSignInClient: GoogleSignInClient
//    val reqCode = 123
    var firebaseAuth = FirebaseAuth.getInstance()

    var imageUri: Uri? = null
    private val appGlobals = AppGlobals()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("nameKey", MODE_PRIVATE)

        // Configure Google Sign In inside onCreate method
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_signIn_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // initialize the firebaseAuth variable
        firebaseAuth = FirebaseAuth.getInstance()

        //Image Click listener
        userImageId.setOnClickListener {
            Log.d("Image view", "Clicked")
            pickImage()
        }

        signInId.setOnClickListener {
            signInGoogle()
        }

        registerNameButton.setOnClickListener {
            setUpNameSendButton()
        }
    }

    var imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {imagePickerResult ->
        if (imagePickerResult.resultCode == Activity.RESULT_OK) {
            val intent= imagePickerResult.data

            if (intent != null) {
                imageUri = intent.data!!

                Glide.with(applicationContext)
                    .load(imageUri)
                    .error(R.drawable.ic_baseline_person_pin_24)
                    .into(userImageId)
            } else {
                Log.e("Error", "Image not set")
            }
        }
    }

    private fun pickImage() {
        Log.e("pick", "Image not set")

        //Check Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED) {
                Log.e("if", "Image not set")

                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, 1001)
            } else {
                val  intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                imagePickerLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
    }
    //On Permission granted this override function will be called
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                pickImage()
            }
            else{
                // Permission was denied
                showAlert("Image permission was denied. Unable to pick image.");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showAlert(message: String) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setPositiveButton("Ok") {_, _ ->
            builder.create().dismiss()
        }
        builder.setTitle("Permission!")
        builder.setMessage(message)
        builder.create().show()
    }

    //
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
        val currentUser = firebaseAuth.currentUser
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setUpNameSendButton() {
        val userName = userNameEditTextId.text.toString()
        val userEmail = registerEmailId.text.toString()
        val userPassword = registerPasswordId.text.toString()
        appGlobals.saveString("userName", userName)

        if (userName.isEmpty() && userEmail.isEmpty() && userPassword.isEmpty()) {
            userNameEditTextId.error = "Name required"
            registerEmailId.error = "email required"
            registerPasswordId.error = "Password required"

        }else {
//            sendName()
            userNameEditTextId.setText("")
            registerEmailId.setText("")
            registerPasswordId.setText("")
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("Main", "Successfully created user with: ${it.result.user?.uid}")
//                SavedPreference.setEmail(this, it.result.user?.email.toString())
//                SavedPreference.setUsername(this, it.result.user.toString())
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.e("Failure","${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUri == null) return
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/ChatApp/$fileName")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                Log.d("Image Storage", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("ImageUri", "File Location: $it")

                    appGlobals.saveString("ProfileImageUrl", it.toString())
                    saveUserToFireBaseDatabase(it.toString())

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Log.d("ImageUri", "File Location failed")
            }
    }

    private fun saveUserToFireBaseDatabase(profileImageUri: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        appGlobals.saveString("UID", uid)

        val ref = FirebaseDatabase.getInstance().getReference("/messages/$uid")

        val userMessages = User(uid, userNameEditTextId.text.toString(), profileImageUri)

//        ref.setValue(userMessages)
//            .addOnSuccessListener {
//                Log.d("Data to FBDB", "Successfully added")
//            }
    }

    class User(val uid: String, val userName: String, val profileImageUri: String)

    @SuppressLint("CommitPrefEdits")
    private fun sendName() {
        val name = userNameEditTextId.text.toString().trim()

//        sharedPreferencesEditor = sharedPreferences.edit()
//        sharedPreferencesEditor.putString("Name", name)
//        sharedPreferencesEditor.apply()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("Name", name)
        }
        startActivity(intent)
        finish()
    }
}