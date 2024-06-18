package com.example.hiweather_aos.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.MainActivity
import com.example.hiweather_aos.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 로그인 버튼
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            signIn(email, password)
        }

        // 회원가입 페이지로 이동
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 구글 로그인 로직
        binding.btnGoogleLogin.setOnClickListener {
            // 구글 로그인 구현
        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            Toast.makeText(
                baseContext, "로그아웃합니다.",
                Toast.LENGTH_SHORT
            ).show()
            auth.signOut()
            updateUI(null)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) { // 로그인 상태
            binding.tvUsername.text = "${user.email} 님 안녕하세요!"
            binding.etEmail.visibility = View.GONE
            binding.etPassword.visibility = View.GONE
            binding.btnLogin.visibility = View.GONE
            binding.btnRegister.visibility = View.GONE
            binding.btnGoogleLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
        } else { // 로그아웃 상태
            binding.tvUsername.visibility = View.INVISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.VISIBLE
            binding.btnGoogleLogin.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
        }
    }
}
