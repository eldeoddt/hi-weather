package com.example.hiweather_aos.Activity

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.R
import com.example.hiweather_aos.databinding.ActivityLogoutConfirmationBinding

class LogoutConfirmationActivity : AppCompatActivity() {
    lateinit var binding:ActivityLogoutConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그아웃 버튼 클릭 시 처리
        binding.btnLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("is_logged_in", false)
            editor.apply()

            // 설정 페이지로 돌아가기
            finish()
        }
    }
}