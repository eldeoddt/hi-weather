package com.example.hiweather_aos.tempService
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TempService {
    @GET("getUltraSrtNcst")
    suspend fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("dataType") dataType: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<ResponseBody>
}
