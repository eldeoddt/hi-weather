package com.example.hiweather_aos.mainWeatherService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainWeatherService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getWeatherData()
        return START_STICKY
    }

    private fun getWeatherData() {
        val serviceKey = "NbNN1unD%2BiIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt%2F5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ%3D%3D"
        val numOfRows = 10
        val pageNo = 1
        val baseDate = getCurrentDate()
        val baseTime = "0600"
        val nx = 55
        val ny = 127

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val weatherResponse = MainWeatherClient.instance.getWeather(serviceKey, numOfRows, pageNo, baseDate, baseTime, nx, ny)
                Log.d("WeatherService", "Weather data: ${weatherResponse.response.body.items.item}")
            } catch (e: Exception) {
                Log.e("WeatherService", "Network call failed: ${e.message}")
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
