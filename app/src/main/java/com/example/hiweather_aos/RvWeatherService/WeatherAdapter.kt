package com.example.hiweather_aos.RvWeatherService

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiweather_aos.R


class WeatherAdapter(private val weatherList: List<WeatherItem>, context: Context) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    lateinit var sharedPreferences: SharedPreferences
    private var selectedItems: Set<String> = emptySet() // 날씨 항목
    private var textSize: Float = 14f // 기본 글씨 크기
    private var vecStyle:String = "english" // 기본 풍향 스타일
    private var isTimeVisible: Boolean = true // 기본 시간 보이게 하기
    init {
        loadPreferences(context) // preference 불러오기
        notifyDataSetChanged()
    }

    /**
     * 선택한 아아템을 preferences에서 가져오기.
     */
    private fun loadPreferences(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        selectedItems = sharedPreferences.getStringSet("selected_weather_items", setOf()) ?: setOf()
        Log.d("adapter", "Weather adapter -- selected items: $selectedItems")

        val textSizePreference = sharedPreferences.getString("text_size_preference", "normal")
        textSize = when (textSizePreference) {
            "small" -> 12f
            "large" -> 17f
            else -> 14f
        }
        vecStyle = sharedPreferences.getString("wind_direction_language_preference", "english") ?: "english"
    }

//    fun setWeatherItems(weatherItems: List<WeatherItem>) {
//        this.weatherItems = weatherItems
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherItem = weatherList[position]
        holder.bind(weatherItem, selectedItems, textSize, vecStyle)
    }

    override fun getItemCount() = weatherList.size
    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val tmp: TextView = itemView.findViewById(R.id.tv_tmp)
        val pop: TextView = itemView.findViewById(R.id.tv_pop)
        val pty: TextView = itemView.findViewById(R.id.tv_pty)
        val reh: TextView = itemView.findViewById(R.id.tv_reh)
        val sky: ImageView = itemView.findViewById(R.id.img_weather)
        val vec: TextView = itemView.findViewById(R.id.tv_vec)
        val wsd: TextView = itemView.findViewById(R.id.tv_wsd)

        fun bind (weatherItem: WeatherItem, selectedItems: Set<String>, textSize: Float, vecStyle: String) {
            Log.d("adapter", "weather adapter -- selected items : ${selectedItems}")
            val showTmp = selectedItems.contains("tmp")
            val showPop = selectedItems.contains("pop")
            val showPty = selectedItems.contains("pty")
            val showReh = selectedItems.contains("reh")
            val showVec = selectedItems.contains("vec")
            val showWsd = selectedItems.contains("wsd")

            time.text = "${weatherItem.time.substring(0, 2)}시"
            tmp.visibility = if (showTmp) View.VISIBLE else View.GONE
            pop.visibility = if (showPop) View.VISIBLE else View.GONE
            pty.visibility = if (showPty) View.VISIBLE else View.GONE
            reh.visibility = if (showReh) View.VISIBLE else View.GONE
            vec.visibility = if (showVec) View.VISIBLE else View.GONE
            wsd.visibility = if (showWsd) View.VISIBLE else View.GONE

            if (showTmp) tmp.text = "• 기온 : ${weatherItem.tmp}°"
            if (showPop) pop.text = "• 강수확률 : ${weatherItem.pop}%"
            if (showPty) pty.text = "• 강수량 : ${weatherItem.pty}mm"
            if (showReh) reh.text = "• 습도 : ${weatherItem.reh}%"
            if (showVec) vec.text = "• 풍향 : ${getWindDirection(weatherItem.vec.toInt(), vecStyle)}"
            if (showWsd) wsd.text = "• 풍속 : ${weatherItem.wsd}m/s"

            // 하늘 상태에 따라 아이콘 설정
            val skyValue = weatherItem.sky.toIntOrNull()
            Log.d("fraglog", "main weather adapter --- sky value: $skyValue")
            when (skyValue) {
                1 -> sky.setImageResource(R.drawable.ic_sun)
                3 -> sky.setImageResource(R.drawable.ic_cloudy)
                4 -> sky.setImageResource(R.drawable.ic_dim)
                else -> sky.setImageResource(R.drawable.ic_sun)
            }

            // 글씨 크기 설정
            time.textSize = textSize
            tmp.textSize = textSize
            pop.textSize = textSize
            pty.textSize = textSize
            reh.textSize = textSize
            vec.textSize = textSize
            wsd.textSize = textSize
            Log.d("fraglog", "weather adapter --- " +
                    "time: ${weatherItem.time}, tmp: ${weatherItem.tmp}, " +
                    "POP: ${weatherItem.pop}, PTY: ${weatherItem.pty},REH: ${weatherItem.reh},VEC: ${weatherItem.vec},WSD: ${weatherItem.wsd}")

            Log.d("fraglog", "weather adapter --- 글씨크기 : ${textSize}")
        }

        /**
         * 풍향 변환 wind direc, vec
         */
        private fun getWindDirection(degree: Int, language: String): String {
            val directionsEnglish = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
            val directionsKorean = arrayOf("북", "북북동", "북동", "동북동", "동", "동남동", "남동", "남남동", "남", "남남서", "남서", "서남서", "서", "서북서", "북서", "북북서")

            val index = ((degree + 11.25) / 22.5).toInt() % 16
            return if (language == "korean") directionsKorean[index] else directionsEnglish[index]
        }
    }
}