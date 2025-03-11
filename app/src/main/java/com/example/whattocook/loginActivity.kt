package com.example.whattocook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class loginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;
    private  lateinit var email : EditText;
    private lateinit var password : EditText
    private  lateinit var sign_in : Button
// ...
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        email = findViewById(R.id.sign_in_email)
        password = findViewById(R.id.sign_in_password)
        sign_in = findViewById(R.id.sign_button)

        sign_in.setOnClickListener {
            // Get the email and password input
            val emailInput = email.text.toString()
            val passwordInput = password.text.toString()

            // Validate input
            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                // Show an error message if fields are empty
                Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Exit the click listener
            }

            // If input is valid, proceed with sign-in logic
            performSignIn(emailInput, passwordInput)
        }
    }

    private fun performSignIn(emailInput: String, passwordInput: String) {

        auth.signInWithEmailAndPassword(emailInput,passwordInput).addOnSuccessListener {
            Toast.makeText(applicationContext, " success login!", Toast.LENGTH_SHORT).show()
            GotoHome()
        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext, "failed to login "+exception, Toast.LENGTH_SHORT).show()
        }
    }
    private fun GotoHome(){

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
      //  updateUI(currentUser)
    }
}