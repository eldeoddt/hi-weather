package com.example.hiweather_aos.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.databinding.ActivityLogoutConfirmationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutConfirmationActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogoutConfirmationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 로그아웃 버튼 클릭 시 처리
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            clearLoginState()

            // 로그인 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun clearLoginState() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.apply()
    }
}
