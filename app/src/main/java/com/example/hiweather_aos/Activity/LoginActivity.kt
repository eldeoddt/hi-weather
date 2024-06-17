package com.example.hiweather_aos.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.R
import com.example.hiweather_aos.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // 이메일과 비밀번호를 가져와서 로그인 처리
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            // 로그인 로직 구현
            if (login(email, password)) {
                // 로그인 성공
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_logged_in", true)
                editor.apply()
                finish()
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        // 구글 로그인 로직
        binding.btnGoogleLogin.setOnClickListener {

        }

        // 회원가입 페이지로 이동
        binding.btnRegister.setOnClickListener {

        }
    }

    private fun login(email: String, password: String): Boolean {
        // 실제 로그인 로직 구현
        return email == "test@example.com" && password == "password"
    }
}