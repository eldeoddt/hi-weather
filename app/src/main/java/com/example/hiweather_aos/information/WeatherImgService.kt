import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherImgService {
    @GET("getInsightSatlit")
    suspend fun getSatelliteImages(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("sat") sat: String,
        @Query("data") data: String,
        @Query("area") area: String,
        @Query("time") time: String,
        @Query("dataType") dataType: String
    ): Response<ResponseBody>
}
