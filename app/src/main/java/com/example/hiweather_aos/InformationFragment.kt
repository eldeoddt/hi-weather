package com.example.hiweather_aos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.hiweather_aos.databinding.FragmentInformationBinding
import com.example.hiweather_aos.information.WeatherImgClient
import com.example.hiweather_aos.information.WeatherImgResponse
import com.example.hiweather_aos.mainWeatherService.MainWeatherClient
import com.example.hiweather_aos.mainWeatherService.MainWeatherResponse
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class InformationFragment : Fragment() {
    lateinit var binding: FragmentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchWeatherData()
        setupYouTubePlayer()
        fetchSatelliteImage() // 이미지 불러오기
    }

    private fun fetchSatelliteImage() {
        val serviceKey = "NbNN1unD+iIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt/5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ=="
        val numOfRows = 10
        val pageNo = 1
        val sat = "g2"
        val data = "rgbt"
        val area = "ko"
        val time = getYesterdayDate()
        val dataType = "json"

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    WeatherImgClient.instance.getSatelliteImages(
                        serviceKey, numOfRows, pageNo, sat, data, area, time, dataType
                    )
                }

                val responseBodyString = response.body()?.string()
                Log.d("InformationFragment", "img -- Response Body: $responseBodyString")

                val gson = Gson()
                val weatherImgResponse = gson.fromJson(responseBodyString, WeatherImgResponse::class.java)
                if (weatherImgResponse != null && weatherImgResponse.response.body != null) {
                    val items = weatherImgResponse.response.body.items.item
                    if (!items.isNullOrEmpty()) {
                        val satImgCFile = items[0].satImgCFile
                        if (!satImgCFile.isNullOrBlank()) {
                            val imageUrl = satImgCFile.split(",")[0].trim().replace("[", "").replace("]", "")
                            Log.d("InformationFragment", "Image URL: $imageUrl")

                            withContext(Dispatchers.Main) {
                                Glide.with(this@InformationFragment)
                                    .load(imageUrl)
                                    .into(binding.ivInfoImg)
                            }
                        } else {
                            Log.e("InformationFragment", "satImgCFile is null or blank")
                        }
                    } else {
                        Log.e("InformationFragment", "Items are null or empty")
                    }
                } else {
                    Log.e("InformationFragment", "Response body is null or malformed")
                }
            } catch (e: HttpException) {
                Log.e("InformationFragment", "Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("InformationFragment", "Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }




    private fun setupYouTubePlayer() {
        lifecycle.addObserver(binding.youtubePlayerView)

        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // 여기에 원하는 YouTube 비디오 ID를 입력합니다.
//                val videoId = "dQw4w9WgXcQ"
                val videoId = "ryRIrmtV3CQ"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    private fun fetchWeatherData() {
        val serviceKey = "NbNN1unD+iIkxInJJnWYzOMcfOGodI7Rggle84PcNGdjVbFhgt/5S1JEcPLKM3ycoWZYfdYELKgzuIdTwOykeQ=="
        val dataType = "json"
        val numOfRows = 350
        val baseDate = getCurrentDate()
        val baseTime = getCurrentHour()
        val nx = 55
        val ny = 127

        val (adjustDate, adjustedTime) = adjustBaseTimeAndDate(baseDate, baseTime)

        lifecycleScope.launch {
            try {
                val requestUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                        "?serviceKey=$serviceKey" +
                        "&dataType=$dataType" +
                        "&numOfRows=$numOfRows" +
                        "&pageNo=1" +
                        "&base_date=$adjustDate" +
                        "&base_time=$adjustedTime" +
                        "&nx=$nx" +
                        "&ny=$ny"

                Log.d("InformationFragment", "Request URL: $requestUrl")

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

                val responseBodyString = response.string()
                Log.d("InformationFragment", "Response Body: $responseBodyString")

                val gson = Gson()
                val mainWeatherResponse = gson.fromJson(responseBodyString, MainWeatherResponse::class.java)
                if (mainWeatherResponse != null && mainWeatherResponse.response != null && mainWeatherResponse.response.body != null) {
                    val items = mainWeatherResponse.response.body.items.item
                    val filteredItems = items.filter { it.category == "TMP" }
                    val tmpValues = filteredItems.map { it.fcstTime to it.fcstValue.toFloat() }
                    displayChart(tmpValues)
                } else {
                    Log.e("InformationFragment", "Response body is null or malformed")
                }

            } catch (e: HttpException) {
                Log.e("InformationFragment", "Network call failed: ${e.message()}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("InformationFragment", "Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun displayChart(tmpValues: List<Pair<String, Float>>) {
        // tmpValues 리스트에서 현재 시간을 기준으로 데이터를 정렬합니다.
        val sortedTmpValues = tmpValues.sortedBy { it.first }

        val entries = sortedTmpValues.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        val dataSet = LineDataSet(entries, "Temperature")
        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData

        // X축 레이블 설정
        val xAxis = binding.lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in sortedTmpValues.indices) {
                    sortedTmpValues[index].first
                } else {
                    ""
                }
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)

        binding.lineChart.invalidate() // refresh chart
    }

    private fun adjustBaseTimeAndDate(currentDate: String, currentTime: String): Pair<String, String> {
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
        var adjustedTime = timeMapping[currentTime] ?: currentTime

        if (currentTime == "0000" || currentTime == "0100" || currentTime == "0200" || currentTime == "0300") {
            adjustedDate = getYesterdayDate()
        }

        return Pair(adjustedDate, adjustedTime)
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
     * get Current Hour to request
     */
    private fun getCurrentHour(): String {
        val dateFormat = SimpleDateFormat("HH00", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return dateFormat.format(Date())
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
}
