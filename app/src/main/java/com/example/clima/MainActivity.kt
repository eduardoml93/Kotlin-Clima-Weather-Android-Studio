package com.example.clima

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var cityEditText: EditText
    private lateinit var getWeatherButton: Button
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        getWeatherButton = findViewById(R.id.getWeatherButton)
        weatherTextView = findViewById(R.id.weatherTextView)

        getWeatherButton.setOnClickListener {
            val cityName = cityEditText.text.toString()
            if (cityName.isNotEmpty()) {
                getWeatherData(cityName)
            }
        }
    }

    private fun getWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val call = weatherService.getWeather(cityName, "a5676ce9dbe81f9ddad2125c4dedb9b6", "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val weatherInfo = "Cidade: ${it.name}\n" +
                                "País: ${it.sys.country}\n" +
                                "Temperatura: ${it.main.temp}°C\n" +
                                "Sensação térmica: ${it.main.feels_like}°C\n" +
                                "Pressão: ${it.main.pressure} hPa\n" +
                                "Umidade: ${it.main.humidity}%\n" +
                                "Condição: ${it.weather[0].description}"

                        weatherTextView.text = weatherInfo
                    } ?: run {
                        weatherTextView.text = "Resposta vazia"
                    }
                } else {
                    weatherTextView.text = "Erro na resposta: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherTextView.text = "Falha na requisição: ${t.message}"
            }
        })
    }

interface WeatherService {
    @GET("weather")
    fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}

data class WeatherResponse(
    val name: String,
    val sys: Sys,
    val main: Main,
    val weather: List<Weather>
)

data class Sys(
    val country: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)}
