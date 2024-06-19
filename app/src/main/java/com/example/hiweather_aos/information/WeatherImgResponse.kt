package com.example.hiweather_aos.information

import com.google.gson.annotations.SerializedName

data class WeatherImgResponse(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val dataType: String,
    val items: Items,
    val pageNo: Int,
    val numOfRows: Int,
    val totalCount: Int
)

data class Items(
    val item: List<ImageItem>
)

data class ImageItem(
    @SerializedName("satImgC-file")
    val satImgCFile: String
)
