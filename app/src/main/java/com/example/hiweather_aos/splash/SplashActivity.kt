package com.example.hiweather_aos.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.hiweather_aos.MainActivity
import com.example.hiweather_aos.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 2000 // 2초 동안 스플래시 화면을 표시

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // 2초 후 MainActivity로 전환
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // 스플래시 액티비티를 종료하여 뒤로 가기 버튼을 눌러도 스플래시 화면이 보이지 않게 함
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}
