package com.example.hiweather_aos

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // 설정 변경 시 호출되는 메소드
        when (key) {
            "example_switch" -> {
                // 예: 스위치 설정 변경 시 처리
                val isEnabled = sharedPreferences?.getBoolean(key, false) ?: false
                // 변경 사항을 반영하는 로직 추가
            }
            "example_list" -> {
                // 예: 리스트 설정 변경 시 처리
                val value = sharedPreferences?.getString(key, "") ?: ""
                // 변경 사항을 반영하는 로직 추가
            }
            // 다른 설정 항목에 대한 처리 추가
        }
    }
}
