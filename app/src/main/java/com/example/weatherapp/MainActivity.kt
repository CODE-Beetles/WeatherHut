package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var CITY: String
    val API: String = "9641186837d997f3980825f1a9abe57c" // Use API key
    var lat: String = ""
    var lon: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        CITY=cityName.text.toString()
//        weatherTask().execute()
//        aqiData().execute()
        refreshapp()
    }


    private fun refreshapp() {
        swiperefresh.setOnRefreshListener {

            Toast.makeText(this, "Refreshed just now !!", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(getIntent())
            swiperefresh.isRefreshing = false

        }
    }



    inner class aqiData() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response1: String?
            try {
                response1 =
                    URL("https://api.weatherbit.io/v2.0/current/airquality?lat=$lat&lon=$lon&key=540700e7ec4f4fb1910ef7a0fa574bbe").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response1 = null
            }
            return response1
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val data = jsonObj.getJSONArray("data").getJSONObject(0)
                val dataAQI = "AQI : "+data.getString("aqi")
                findViewById<TextView>(R.id.aqiVal).text = dataAQI.capitalize()
                val datapmtwo= "PM2.5: "+ data.getString("pm25")
                findViewById<TextView>(R.id.pm2_5).text = datapmtwo.capitalize()
                val datapmten= "PM10: "+ data.getString("pm10")
                findViewById<TextView>(R.id.pm10).text = datapmten.capitalize()
                val dataso2= "SO2: "+ data.getString("so2")
                findViewById<TextView>(R.id.so2).text = dataso2.capitalize()
                val datano2= "NO2: "+ data.getString("no2")
                findViewById<TextView>(R.id.no2).text = datano2.capitalize()
                val dataco= "CO: "+ data.getString("co")
                findViewById<TextView>(R.id.co).text = dataco.capitalize()
                val datao3= "O3: "+ data.getString("o3")
                findViewById<TextView>(R.id.o3).text = datao3.capitalize()


            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }



    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val coord = jsonObj.getJSONObject("coord")
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                lat = coord.getString("lat")
                lon = coord.getString("lon")

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")+"%"

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")+" km/h"
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }

    fun onClicked(view: View) {
        CITY=cityName.text.toString()
        weatherTask().execute()
        aqiData().execute()
    }

//    inner class aqiData() : AsyncTask<String, Void, String>() {
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//
//        override fun doInBackground(vararg p0: String?): String? {
//            var response1: String?
//            try {
//                response1 =
//                    URL("https://api.weatherbit.io/v2.0/current/airquality?lat=$lat&lon=$lon&key=540700e7ec4f4fb1910ef7a0fa574bbe").readText(
//                        Charsets.UTF_8
//                    )
//            } catch (e: Exception) {
//                response1 = null
//            }
//            return response1
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            try {
//                val jsonObj = JSONObject(result)
//                val data = jsonObj.getJSONArray("data").getJSONObject(0)
//                val dataAQI ="AQI :"+ data.getString("aqi")
//                findViewById<TextView>(R.id.aqiVal).text = dataAQI.capitalize()
//            } catch (e: Exception) {
//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
//            }
//        }
//    }
}
