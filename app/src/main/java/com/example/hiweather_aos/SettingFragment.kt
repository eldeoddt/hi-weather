package com.example.hiweather_aos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.hiweather_aos.Activity.LoginActivity

class SettingFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreference: Preference
    private lateinit var weatherItemsPreference: MultiSelectListPreference
    private lateinit var textSizePreferences: ListPreference
    private lateinit var vecStylePreference: ListPreference
    private lateinit var timeVisibilityPreference:SwitchPreferenceCompat
    private lateinit var postNumPreference: Preference
    private lateinit var mapPreference: Preference

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

        // vec style
        vecStylePreference = findPreference("wind_direction_language_preference")!!
        updateVecStylePreference()

        // time visibleity
        timeVisibilityPreference = findPreference("time_visibility_preference")!!

        // post num
        postNumPreference = findPreference("post_num_preference")!!

        // map
        mapPreference = findPreference("map_preference")!!

        loginPreference.setOnPreferenceClickListener {
            // 로그인 페이지로 이동
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            true
        }

        postNumPreference.setOnPreferenceClickListener {
            // 우편번호 페이지로 이동
            val intent = Intent(activity, PostNumActivity::class.java)
            startActivity(intent)
            true
        }

        mapPreference.setOnPreferenceClickListener {
            // 지도 페이지로 이동
            val mapFragment = MapFragment()

            // FragmentManager를 사용하여 FragmentTransaction 시작
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, mapFragment) // fragment_container는 프래그먼트를 넣을 컨테이너의 ID
                addToBackStack(null) // 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아가기 위해 백스택에 추가
                commit() // 트랜잭션 커밋
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
        if (key == "weather_items_preference") {
            updateWeatherItemsPreference()
        } else if (key == "text_size_preference") {
            upadateTextSizePreference()
        } else if (key == "wind_direction_language_preference"){
            updateVecStylePreference()
        } else if (key == "time_visibility_preference") {
            updateHomeFragmentVisibility()
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
//        Toast.makeText(requireContext(), "글씨 크기 : ${textSizePreferences.value}", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putString("text_size_preference", textSize)
        editor.apply()
    }

    /**
     * update vec style
     */
    fun updateVecStylePreference() {
        val vecStyle = vecStylePreference.value
        Log.d("adapter", "setting vec style -- ${vecStylePreference.value}")
//        Toast.makeText(requireContext(), "풍향 언어 : ${vecStylePreference.value}", Toast.LENGTH_SHORT).show()
        // 선택된 항목을 저장
        val editor = sharedPreferences.edit()
        editor.putString("wind_direction_language_preference", vecStyle)
        editor.apply()
    }

    /**
     * update home fragment time visibility
     */
    private fun updateHomeFragmentVisibility() {
        // 홈 프래그먼트 업데이트를 트리거하기 위해 sharedPreferences 값만 수정
        val isTimeVisible = sharedPreferences.getBoolean("time_visibility_preference", true)
        Log.d("SettingFragment", "updateHomeFragmentVisibility: $isTimeVisible")
    }
}
