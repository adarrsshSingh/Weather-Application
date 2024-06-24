package com.ksc.weatherapp.api

import androidx.core.text.util.LocalePreferences.TemperatureUnit.TemperatureUnits
import com.ksc.weatherapp.models.WeatherApp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {
    @GET("weather/")
    fun getWeatherData(
        @Query("q") cityName: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<WeatherApp>
}