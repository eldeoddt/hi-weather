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
    var selectedItems: Set<String> = emptySet() // 날씨 항목
    var textSize: Float = 14f // 기본 글씨 크기
    init {
        loadWeatherItemsFromPreferences(context)
        loadTextSizeFromPreferences(context)
    }

    /**
     * 선택한 아아템을 preferences에서 가져오기.
     */
    private fun loadWeatherItemsFromPreferences(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        selectedItems = sharedPreferences.getStringSet("selected_weather_items", emptySet()) ?: emptySet()
        Log.d("adapter", "Weather adapter -- selected items: $selectedItems")
    }

    fun loadTextSizeFromPreferences(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val textSizePreference = sharedPreferences.getString("text_size_preference", "normal")
        textSize = when (textSizePreference) {
            "small" -> 12f
            "large" -> 17f
            else -> 14f
        }
    }

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val tmp: TextView = itemView.findViewById(R.id.tv_tmp)
        val pop: TextView = itemView.findViewById(R.id.tv_pop)
        val pty: TextView = itemView.findViewById(R.id.tv_pty)
        val reh: TextView = itemView.findViewById(R.id.tv_reh)
        val sky: ImageView = itemView.findViewById(R.id.img_weather)
        val vec: TextView = itemView.findViewById(R.id.tv_vec)
        val wsd: TextView = itemView.findViewById(R.id.tv_wsd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weatherItem = weatherList[position]

        Log.d("adapter", "weather adapter -- selected items : ${selectedItems}")
        // SharedPreferences에서 사용자가 선택한 항목을 확인
        val showTmp = selectedItems.contains("tmp")
        Log.d("adapter", "adapter -- show tmp의 bool $showTmp")
        val showPop = selectedItems.contains("pop")
        val showPty = selectedItems.contains("pty")
        val showReh = selectedItems.contains("reh")
        val showVec = selectedItems.contains("vec")
        Log.d("adapter", "adapter -- show vec의 bool $showVec")
        val showWsd = selectedItems.contains("wsd")

        holder.time.text = "${weatherItem.time.substring(0, 2)}시"

        holder.tmp.visibility = if (showTmp) View.VISIBLE else View.GONE
        holder.pop.visibility = if (showPop) View.VISIBLE else View.GONE
        holder.pty.visibility = if (showPty) View.VISIBLE else View.GONE
        holder.reh.visibility = if (showReh) View.VISIBLE else View.GONE
        holder.vec.visibility = if (showVec) View.VISIBLE else View.GONE
        holder.wsd.visibility = if (showWsd) View.VISIBLE else View.GONE

        if (showTmp) holder.tmp.text = "• 기온 : ${weatherItem.tmp}°"
        if (showPop) holder.pop.text = "• 강수확률 : ${weatherItem.pop}%"
        if (showPty) holder.pty.text = "• 강수량 : ${weatherItem.pty}mm"
        if (showReh) holder.reh.text = "• 습도 : ${weatherItem.reh}%"
        if (showVec) holder.vec.text = "• 풍향 : ${getWindDirection(weatherItem.vec.toInt())}" // 풍향 변환 함수 사용
        if (showWsd) holder.wsd.text = "• 풍속 : ${weatherItem.wsd}m/s"

        // 하늘 상태에 따라 아이콘 설정
        val skyValue = weatherItem.sky.toIntOrNull()
        Log.d("fraglog", "main weather adapter --- sky value: $skyValue")
        when (skyValue) {

            1 -> holder.sky.setImageResource(R.drawable.ic_sun) // Replace with your sun image resource
            3 -> holder.sky.setImageResource(R.drawable.ic_cloudy) // Replace with your cloudy image resource
            4 -> holder.sky.setImageResource(R.drawable.ic_dim) // Replace with your dim image resource
            else -> holder.sky.setImageResource(R.drawable.ic_sun) // Replace with your default image resource
        }
        holder.time.text = "${weatherItem.time.substring(0, 2)}시"

        // 글씨 크기 설정
        holder.time.textSize = textSize
        holder.tmp.textSize = textSize
        holder.pop.textSize = textSize
        holder.pty.textSize = textSize
        holder.reh.textSize = textSize
        holder.vec.textSize = textSize
        holder.wsd.textSize = textSize

        Log.d("fraglog", "weather adapter --- " +
                "time: ${weatherItem.time}, tmp: ${weatherItem.tmp}, " +
                "POP: ${weatherItem.pop}, PTY: ${weatherItem.pty},REH: ${weatherItem.reh},VEC: ${weatherItem.vec},WSD: ${weatherItem.wsd}")

        Log.d("fraglog", "weather adapter --- 글씨크기 : ${textSize}")
    }

    override fun getItemCount() = weatherList.size

    /**
     * 풍향 변환 wind direc, vec
     */
    fun getWindDirection(degree: Int): String {
        return when {
            degree in 0..22 || degree in 338..360 -> "북"
            degree in 23..67 -> "북동"
            degree in 68..112 -> "동"
            degree in 113..157 -> "동남동"
            degree in 158..202 -> "남"
            degree in 203..247 -> "남남서"
            degree in 248..292 -> "서"
            degree in 293..337 -> "북서"
            else -> "$degree"
        }
    }

}