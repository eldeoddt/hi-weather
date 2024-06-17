package com.example.hiweather_aos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.hiweather_aos.Activity.LoginActivity
import com.example.hiweather_aos.Activity.LogoutConfirmationActivity

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        sharedPreferences = preferenceScreen.sharedPreferences

        loginPreference = findPreference("login_preference")!!
        updateLoginPreference()

        loginPreference.setOnPreferenceClickListener {
            if (isLoggedIn()) {
                // 로그아웃 확인 페이지로 이동
                val intent = Intent(activity, LogoutConfirmationActivity::class.java)
                startActivity(intent)
            } else {
                // 로그인 페이지로 이동
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "login_preference") {
            updateLoginPreference()
        }
    }

    private fun updateLoginPreference() {
        if (isLoggedIn()) {
            loginPreference.title = "로그아웃"
        } else {
            loginPreference.title = "로그인"
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}
