package com.example.hiweather_aos

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "hiweather_channel")
            .setSmallIcon(R.drawable.ic_cloudy)
            .setContentTitle("HiWeather 알림")
            .setContentText("정보 탭에서 시간별 기온 그래프를 확인해보세요!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
