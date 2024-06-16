package com.example.hiweather_aos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hiweather_aos.databinding.FragmentHomeBinding
import com.example.hiweather_aos.tempService.TempClient
import com.example.hiweather_aos.tempService.TempResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchTempData()
        binding.tvDate.text = getCurrentMainTime()

    }

    private fun fetchTempData() {
        val serviceKey = "NbNN1unD%2BiIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt%2F5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ%3D%3D"
        val dataType = "json"
        val numOfRows = 10
        val pageNo = 1
        val baseDate = getCurrentDate()
        val baseTime = getCurrentTime()
        val nx = 55
        val ny = 127

        val url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst" +
                "?serviceKey=$serviceKey" +
                "&dataType=$dataType" +
                "&numOfRows=$numOfRows" +
                "&pageNo=$pageNo" +
                "&base_date=$baseDate" +
                "&base_time=$baseTime" +
                "&nx=$nx" +
                "&ny=$ny"

        Log.d("fraglog", "Request URL: $url")

        lifecycleScope.launch {
            try {
                // Define the logging interceptor
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                // Add the interceptor to the OkHttpClient
                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

                // Create the request
                val request = Request.Builder().url(url).build()

                // Use the client with the logging interceptor
                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() // Corrected to use string() method
                    Log.d("fraglog", "Response Body: $responseBody")

                    if (responseBody != null) {
//                        Log.d("fraglog", "응답이 null이 아님")

                        // 응답 문자열을 JSON 객체로 변환
                        val jsonObject = JsonParser.parseString(responseBody).asJsonObject

                        // JSON 객체를 TempResponse 객체로 변환
                        val tempResponse = Gson().fromJson(jsonObject, TempResponse::class.java)

                        withContext(Dispatchers.Main) {
                            displayTempData(tempResponse)
                        }
                    } else {
                        Log.e("fraglog", "Response body is null")
                    }
                }
            } catch (e: HttpException) {
                Log.e("fraglog", "Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("fraglog", "Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun displayTempData(tempResponse: TempResponse) {
        val t1hItem = tempResponse.response.body.items.item.find { it.category == "T1H" }
        val t1hValue = t1hItem?.obsrValue ?: "N/A"
        Log.d("fraglog", "temp: $t1hValue")

        activity?.runOnUiThread {
            binding.tvCurTemp.text = "$t1hValue"
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    private fun getCurrentTime() : String {
        val dateFormat = SimpleDateFormat("HH00", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    private fun getCurrentMainTime() : String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val dateStr = dateFormat.format(Date())

        val year = dateStr.substring(0, 4)
        val month = dateStr.substring(4, 6)
        val day = dateStr.substring(6, 8)

        return "${year}년 ${month}월 ${day}일"
    }
}