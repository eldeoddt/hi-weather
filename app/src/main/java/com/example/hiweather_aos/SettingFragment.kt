package com.example.hiweather_aos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiweather_aos.Activity.LoginActivity
import com.example.hiweather_aos.Activity.LogoutConfirmationActivity
import com.example.hiweather_aos.RvWeatherService.WeatherAdapter

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: Preference
    private lateinit var weatherItemsPreference: MultiSelectListPreference
    lateinit var textSizePreferences: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        sharedPreferences = preferenceScreen.sharedPreferences

        // login pref
        loginPreference = findPreference("login_preference")!!

        //weather item pref
        weatherItemsPreference = findPreference("weather_items_preference")!!
        updateWeatherItemsPreference()

        //text size
        textSizePreferences = findPreference("text_size_preference")!!
        upadateTextSizePreference()

        loginPreference.setOnPreferenceClickListener {
            // 로그인 페이지로 이동
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
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
        if (key == "weather_items_preference") {
            updateWeatherItemsPreference()
        } else if (key == "text_size_preference") {
            upadateTextSizePreference()
        }
    }

    /**
     * update weather item list
     */
    private fun updateWeatherItemsPreference() {
        val selectedItems = weatherItemsPreference.values
        Log.d("adapter", "selectedItems -- ${selectedItems}")
        // 선택된 항목을 SharedPreferences에 저장
        val editor = sharedPreferences.edit()
        editor.putStringSet("selected_weather_items", selectedItems)
        editor.apply()
    }

    /**
     * update text size
     */
    fun upadateTextSizePreference() {
        val textSize = textSizePreferences.value
        Log.d("adapter", "setting text size -- ${textSizePreferences.value}")
        Toast.makeText(requireContext(), "글씨 크기 : ${textSizePreferences.value}", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putString("text_size_preference", textSize)
        editor.apply()
    }
}
