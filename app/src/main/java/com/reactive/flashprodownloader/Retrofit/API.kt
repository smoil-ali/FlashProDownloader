package com.reactive.lightvideodownloader.Retrofit


import com.reactive.flashprodownloader.model.FlashDailymotionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface API {
    @GET
    fun getResponse(@Url url: String):Call<FlashDailymotionResponse>

    @GET("https://alltubedownload.net/json")
    fun getVimeoResponse(@Query("url") url: String):Call<String>


}