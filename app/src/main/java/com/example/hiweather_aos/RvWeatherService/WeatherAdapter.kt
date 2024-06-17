package com.example.hiweather_aos.RvWeatherService

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiweather_aos.R


class WeatherAdapter(private val weatherList: List<WeatherItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

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
        // 하늘 상태에 따라 아이콘 설정
        val skyValue = weatherItem.sky.toIntOrNull()
        when (skyValue) {
            1 -> holder.sky.setBackgroundResource(R.drawable.ic_sun) // Replace with your sun image resource
            3 -> holder.sky.setBackgroundResource(R.drawable.ic_cloudy) // Replace with your cloudy image resource
            4 -> holder.sky.setBackgroundResource(R.drawable.ic_dim) // Replace with your dim image resource
            else -> holder.sky.setBackgroundResource(R.drawable.ic_sun) // Replace with your default image resource
        }
        holder.time.text = "${weatherItem.time.substring(0, 1)}시"
        holder.tmp.text = weatherItem.tmp
        holder.pop.text = weatherItem.pop
        holder.pty.text = weatherItem.pty
        holder.reh.text = weatherItem.reh
        holder.vec.text = weatherItem.vec
        holder.wsd.text = weatherItem.wsd
        Log.d("fraglog", "weather adapter --- " +
                "time: ${weatherItem.time}, tmp: ${weatherItem.tmp}, " +
                "POP: ${weatherItem.pop}, PTY: ${weatherItem.pty},REH: ${weatherItem.reh},VEC: ${weatherItem.vec},WSD: ${weatherItem.wsd}")
    }

    override fun getItemCount() = weatherList.size
}