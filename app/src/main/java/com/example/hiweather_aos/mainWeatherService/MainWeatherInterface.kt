package com.example.hiweather_aos.mainWeatherService

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("getUltraSrtNcst")
    suspend fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): MainWeatherResponse
}