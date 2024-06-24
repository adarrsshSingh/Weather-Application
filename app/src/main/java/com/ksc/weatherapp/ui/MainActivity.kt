package com.ksc.weatherapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ksc.weatherapp.R
import com.ksc.weatherapp.api.WeatherApiInterface
import com.ksc.weatherapp.databinding.ActivityMainBinding
import com.ksc.weatherapp.models.WeatherApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.SimpleFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
        }
        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sign_out -> {
                    auth.signOut()
                    startActivity(Intent(this, LogInActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }




        searchCity()
        fetchWeatherData("Bangalore")
    }

    private fun setBackAccordingToCondition(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(WeatherApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "505d281a810860d6473cb34f10bfd430", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                // Hide ProgressBar
                binding.progressBar.visibility = View.GONE
                binding.main.visibility = View.VISIBLE
                val responseBody = response.body()
                if (responseBody != null) {
                    val temperature = responseBody.main.temp
                    val tempMin = responseBody.main.temp_min
                    val tempMax = responseBody.main.temp_max
                    val pressure = responseBody.main.pressure
                    val humidity = responseBody.main.humidity
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val windSpeed = responseBody.wind.speed
                    val weatherType = responseBody.weather.firstOrNull()?.main

                    binding.tvTemperature.text = "${temperature.toInt()} °C"
                    binding.tvMinTemp.text = "Min Temp: ${tempMin.toInt()} °C"
                    binding.tvMaxTemp.text = "Max Temp: ${tempMax.toInt()} °C"
                    binding.flowSea.text = "${pressure} hPa"
                    binding.flowHumidity.text = "${humidity}%"
                    binding.flowWind.text = "${windSpeed} m/s"
                    binding.flowSunrise.text = setTimes(sunrise)
                    binding.flowSunset.text = setTimes(sunset)
                    binding.flowConditions.text = weatherType
                    binding.tvCityName.text = "  $cityName"
                    binding.tvDate.text = setDate()
                    binding.tvDay.text = setDay()
                    binding.tvWeather.text = weatherType
                    setBackAccordingToCondition(weatherType.toString())
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                // Hide ProgressBar in case of failure
                binding.progressBar.visibility = View.GONE
                binding.main.visibility = View.VISIBLE
                Log.i("responseError", "error")
            }
        })
    }

    private fun setTimes(sunrise: Long): String {
        val formatter = SimpleDateFormat("HH:MM", Locale.getDefault())
        return formatter.format(Date(sunrise * 1000))

    }

    private fun setDate(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun setDay(): String {
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        return formatter.format(Date())
    }
}