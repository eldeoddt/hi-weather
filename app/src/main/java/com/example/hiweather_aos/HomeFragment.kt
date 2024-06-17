package com.example.hiweather_aos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hiweather_aos.RvWeatherService.WeatherAdapter
import com.example.hiweather_aos.RvWeatherService.WeatherItem
import com.example.hiweather_aos.databinding.FragmentHomeBinding
import com.example.hiweather_aos.mainWeatherService.Item
import com.example.hiweather_aos.mainWeatherService.MainWeatherClient
import com.example.hiweather_aos.mainWeatherService.MainWeatherResponse
import com.example.hiweather_aos.mainWeatherService.MainWeatherService
import com.example.hiweather_aos.tempService.TempClient.TempClient
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
import retrofit2.Call
import retrofit2.HttpException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    var isRain:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchTempData() // 현재 온도, 강수량 불러오기
        binding.tvDate.text = getCurrentMainTime() // 메인 날짜 설정
        fetchMinMaxTemp()// 최고, 최저온도 불러오기
        fetchWeatherData() // 리사이클러뷰 데이터 블러오기

    }

    /**
     * fetch rv weather data - 단기예보
     */
    fun fetchWeatherData() {
        val serviceKey = "NbNN1unD+iIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt/5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ=="
        val dataType = "json"
        var numOfRows = 350
        var baseDate = getCurrentDate()
        val baseTime = getCurrentHour()
        var nx = 55
        var ny = 127

        val (adjustDate, adjustedTime) = adjustBaseTimeAndDate(baseDate, baseTime)

        lifecycleScope.launch {
            try {
                // URL 생성
                val requestUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                        "?serviceKey=NbNN1unD%2BiIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt%2F5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ%3D%3D" +
                        "&dataType=$dataType" +
                        "&numOfRows=$numOfRows" +
                        "&pageNo=1" +
                        "&base_date=$adjustDate" +
                        "&base_time=$adjustedTime" +
                        "&nx=$nx" +
                        "&ny=$ny"

                Log.d("fraglog", "main rv weather --- Request URL: $requestUrl")

                //요청
                val response = withContext(Dispatchers.IO) {
                    MainWeatherClient.instance.getWeather(
                        serviceKey,
                        dataType,
                        numOfRows,
                        1,
                        adjustDate,
                        adjustedTime,
                        nx,
                        ny
                    )
                }

                val responseBodyString = response.string()  // 응답을 문자열로 변환
                Log.d("fraglog", "rv  --- Response Body: $responseBodyString")  // 응답을 로그로 출력

                // JSON 파싱
                val gson = Gson()
                val mainWeatherResponse = gson.fromJson(responseBodyString, MainWeatherResponse::class.java)
                if(mainWeatherResponse != null && mainWeatherResponse.response != null && mainWeatherResponse.response.body != null) {
                    val items = mainWeatherResponse.response.body.items.item
                    val weatherItems = parseWeatherItems(items)
                    val weatherAdapter = WeatherAdapter(weatherItems) // 어댑터 객체 생성
                    binding.rvWeather.adapter = weatherAdapter // 어댑터 붙이기

                } else {
                    Log.e("fraglog", "rv --- Response body is null or malformed")
                }

            } catch (e: HttpException) {
                Log.e("fraglog", "rv --- Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("fraglog", "rv --- Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * parse main weather = fetch weather data
     */
    private fun parseWeatherItems(items: List<Item>): List<WeatherItem> {
        val weatherItems = mutableListOf<WeatherItem>()
        val groupedItems = items.groupBy { it.fcstTime }

        for (time in groupedItems.keys.sorted()) {
            val itemGroup = groupedItems[time] ?: continue

            val tmp = itemGroup.find { it.category == "TMP" }?.fcstValue ?: "-"
            val pop = itemGroup.find { it.category == "POP" }?.fcstValue ?: "-"
            val pty = itemGroup.find { it.category == "PTY" }?.fcstValue ?: "-"
            val reh = itemGroup.find { it.category == "REH" }?.fcstValue ?: "-"
            val sky = itemGroup.find { it.category == "SKY" }?.fcstValue ?: "-"
            val vec = itemGroup.find { it.category == "VEC" }?.fcstValue ?: "-"
            val wsd = itemGroup.find { it.category == "WSD" }?.fcstValue ?: "-"

            weatherItems.add(WeatherItem(time, tmp, pop, pty, reh, sky, vec, wsd))
        }

        return weatherItems
    }

    /**
     * adjust time to request - fetch weather data
     */
    fun adjustBaseTimeAndDate(currentDate: String, currentTime: String): Pair<String, String> {
        // 요청 시간 매핑
        val timeMapping = mapOf(
            "0200" to "2300", "0300" to "2300", "0400" to "0200",
            "0500" to "0200", "0600" to "0200", "0700" to "0500",
            "0800" to "0500", "0900" to "0500", "1000" to "0800",
            "1100" to "0800", "1200" to "0800", "1300" to "1100",
            "1400" to "1100", "1500" to "1100", "1600" to "1400",
            "1700" to "1400", "1800" to "1400", "1900" to "1700",
            "2000" to "1700", "2100" to "1700", "2200" to "2000",
            "2300" to "2000", "0000" to "2000", "0100" to "2300"
        )

        var adjustedDate = currentDate
        var adjustedTime = timeMapping[currentTime] ?: currentTime // 매핑 적용

        // 1시 2시 또는 3시이면 요청 날짜를 어제로 변경한다.
        if (currentTime == "0100" || currentTime == "0200" || currentTime == "0300") {
            adjustedDate = getYesterdayDate()
        }

        return Pair(adjustedDate, adjustedTime)
    }

    /**
     * fetch min max temp - 단기예보
     */
    private fun fetchMinMaxTemp() {
        // 한 번 encoding되기 때문에 decoding 을 넣어줘야 한다. --오류 해결.
        val serviceKey = "NbNN1unD+iIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt/5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ=="
        val dataType = "json"
        var numOfRows = 200
        var baseDate = getCurrentDate()
        val baseTime = getCurrentTime()
        var nx = 55
        var ny = 127

        if (baseTime < "0200") {
            baseDate = getYesterdayDate()
        }

        lifecycleScope.launch {
            try {
                val requestBaseTime = if (baseTime < "0200") "2300" else "0200"
                val requestBaseDate = if (baseTime < "0200") getYesterdayDate() else getCurrentDate()

                // URL 생성
                val requestUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                        "?serviceKey=NbNN1unD%2BiIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt%2F5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ%3D%3D" +
                        "&dataType=$dataType" +
                        "&numOfRows=$numOfRows" +
                        "&pageNo=1" +
                        "&base_date=$requestBaseDate" +
                        "&base_time=$requestBaseTime" +
                        "&nx=$nx" +
                        "&ny=$ny"

                Log.d("fraglog", "min max --- Request URL: $requestUrl")

                val response = withContext(Dispatchers.IO) {
                    MainWeatherClient.instance.getWeather(
                        serviceKey,
                        dataType,
                        numOfRows,
                        1,
                        requestBaseDate,
                        requestBaseTime,
                        nx,
                        ny
                    )
                }

                val responseBodyString = response.string()  // 응답을 문자열로 변환
                Log.d("fraglog", "min max --- Response Body: $responseBodyString")  // 응답을 로그로 출력

                // JSON 파싱
                val gson = Gson()
                val mainWeatherResponse = gson.fromJson(responseBodyString, MainWeatherResponse::class.java)
                displayMinMaxData(mainWeatherResponse)

            } catch (e: HttpException) {
                Log.e("fraglog", "min max --- Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("fraglog", "min max --- Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    /**
     * display min max data
     */
    private fun displayMinMaxData(Response: MainWeatherResponse) {
        val tmnItem = Response.response.body.items.item.find { it.category == "TMN" }
        val tmxItem = Response.response.body.items.item.find { it.category == "TMX" }
        val tmnValue = tmnItem?.fcstValue ?: "N/A"
        val tmxValue = tmxItem?.fcstValue ?: "N/A"
        Log.d("fraglog", "TMN: $tmnValue, TMX: $tmxValue")

        val tmnValueStr = tmnValue.split(".")[0] // 소수점 제거
        val tmxValueStr = tmxValue.split(".")[0]
        activity?.runOnUiThread {
            binding.tvTmn.text = "$tmnValueStr"
            binding.tvTmx.text = "$tmxValueStr"
        }
    }

    /**
     * get yesterday date for min max
     */
    private fun getYesterdayDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    /**
     * fetch current temp data
     */
    private fun fetchTempData() {
        // 한 번 encoding되기 때문에 decoding 을 넣어줘야 한다.
        val serviceKey = "NbNN1unD+iIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt/5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ=="
        val dataType = "json"
        val numOfRows = 10
        val pageNo = 1
        val baseDate = getCurrentDate()
        val baseTime = getAdjustedTime()
        Log.d("fraglog", "temp --- base time: $baseTime")
        val nx = 55
        val ny = 127

        lifecycleScope.launch {
            try {
                val requestUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst" +
                        "?serviceKey=NbNN1unD%2BiIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt%2F5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ%3D%3D" +
                        "&dataType=$dataType" +
                        "&numOfRows=$numOfRows" +
                        "&pageNo=$pageNo" +
                        "&base_date=$baseDate" +
                        "&base_time=$baseTime" +
                        "&nx=$nx" +
                        "&ny=$ny"

                Log.d("fraglog", "temp --- Request URL: $requestUrl")

                val response = withContext(Dispatchers.IO) {
                    TempClient.instance.getTemp(
                        serviceKey,
                        dataType,
                        numOfRows,
                        1,
                        baseDate,
                        baseTime,
                        nx,
                        ny
                    )
                }

                val responseBodyString = response.string() // 응답을 문자열로 변환
                Log.d("fraglog", "temp --- Response Body: $responseBodyString") // 응답을 로그로 출력

                // Json 파싱
                val gson = Gson()
                val tempResponse = gson.fromJson(responseBodyString, TempResponse::class.java)

                if (tempResponse != null && tempResponse.response != null && tempResponse.response.body != null) {
                    val items = tempResponse.response.body.items
                    if (items != null) {
                        displayTempData(tempResponse)
                    } else {
                        Log.e("fraglog", "temp --- Items are null")
                    }
                } else {
                    Log.e("fraglog", "temp --- Response body is null or malformed")
                }

            } catch (e: HttpException) {
                Log.e("fraglog", "temp --- Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("fraglog", "temp --- Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * display current Temp Data
     */
    private fun displayTempData(tempResponse: TempResponse) {
        val t1hItem = tempResponse.response.body.items.item.find { it.category == "T1H" }
        val t1hValue = t1hItem?.obsrValue ?: "N/A"
        Log.d("fraglog", "temp: $t1hValue")

        val t1hValueStr = t1hValue.split(".")[0] // 소수점 제거

        // 초단기 강수형태 불러오기
        val ptyItem = tempResponse.response.body.items.item.find { it.category == "PTY" }
        val ptyValue = ptyItem?.obsrValue ?: "N/A"
        Log.d("fraglog", "temp -- ptyval: $ptyValue")

        // 눈/비 오는지 전역변수 설정
        isRain = ptyValue.toIntOrNull()?.let { it != 0 } ?: false

        activity?.runOnUiThread {
            binding.tvCurTemp.text = "${t1hValueStr}°"

            // 1,5면 비, 2,3,6이면 눈 이미지 표시.
            when (ptyValue.toIntOrNull()) {
                1, 5 -> binding.ivMainSky.setImageResource(R.drawable.ic_rain) // Replace with your rain image resource
                2, 3, 6 -> binding.ivMainSky.setImageResource(R.drawable.ic_snow) // Replace with your snow image resource
                else -> binding.ivMainSky.setImageResource(R.drawable.ic_sun) // Replace with your default image resource
            }
        }
    }

    /**
     * temp data - 요청 시간이 40분 이상이어야 하는 조건 충족하기
     */
    fun getAdjustedTime(): String {
        val currentTime = getCurrentTime()
        val hour = currentTime.substring(0, 2).toInt()
        val minute = currentTime.substring(2, 4).toInt()

        return if (minute >= 40) {
            // mm이 40 이상이면 같은 시간의 "hh00"
            String.format("%02d00", hour)
        } else {
            // mm이 40 미만이면 한 시간 전의 "hh00"
            val adjustedHour = if (hour == 0) 23 else hour - 1
            String.format("%02d00", adjustedHour)
        }
    }

    /**
     * get Current Date to request
     */
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    /**
     * get Current Time to request
     */
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HHmm", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    /**
     * get Current Hour to request
     */
    private fun getCurrentHour(): String {
        val dateFormat = SimpleDateFormat("HH00", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
    }

    /**
     * get Time for main time view
     */
    private fun getCurrentMainTime(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val dateStr = dateFormat.format(Date())

        val year = dateStr.substring(0, 4)
        val month = dateStr.substring(4, 6)
        val day = dateStr.substring(6, 8)

        return "${year}년 ${month}월 ${day}일"
    }
}
