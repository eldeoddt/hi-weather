package com.example.hiweather_aos.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.MainActivity
import com.example.hiweather_aos.databinding.ActivityCreateUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateUserBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 회원가입 버튼 클릭
        binding.btnRegister.setOnClickListener {
            Log.d("auth", "회원가입 버튼 클릭")
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("auth", "이메일, 비밀번호가 비어있지 않음.")
                createAccount(email, password)
            } else {
                Toast.makeText(
                    baseContext,
                    "Please enter email and password.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("auth", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    moveMainPage(auth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("auth", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        // Update UI with the user's information if user is not null
        if (user != null) {
            Toast.makeText(
                baseContext,
                "회원 가입 성공. ${user.email}님 안녕하세요!",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun reload() {
        // Logic to handle reloading the user information, if necessary
        val user = auth.currentUser
        updateUI(user)
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
