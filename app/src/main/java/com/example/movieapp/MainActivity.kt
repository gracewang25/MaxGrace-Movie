package com.example.movieapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var moviePoster: ImageView
    private lateinit var movieTitle: TextView
    private lateinit var movieYear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        moviePoster = findViewById(R.id.moviePoster)
        movieTitle = findViewById(R.id.movieTitle)
        movieYear = findViewById(R.id.movieYear)

        // Set OnClickListener for the search button
        searchButton.setOnClickListener {
            val movieTitle = searchEditText.text.toString()
            searchMovie(movieTitle)
        }
    }

    private fun searchMovie(title: String) {
        // Build the URL for the OMDB API request
        val url = "http://www.omdbapi.com/?t=${title.replace(" ", "+")}&apikey=8ce9f2e8"

        // Create a request using OkHttp
        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure (e.g., show a toast message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                response.body?.close()

                if (response.isSuccessful && responseBody != null) {
                    // Simple parsing of JSON response (consider using Gson or Moshi for complex data)
                    val titleRegex = """"Title":"(.*?)"""".toRegex()
                    val yearRegex = """"Year":"(.*?)"""".toRegex()
                    val posterRegex = """"Poster":"(.*?)"""".toRegex()

                    val titleMatch = titleRegex.find(responseBody)
                    val yearMatch = yearRegex.find(responseBody)
                    val posterMatch = posterRegex.find(responseBody)

                    val title = titleMatch?.groups?.get(1)?.value ?: "N/A"
                    val year = yearMatch?.groups?.get(1)?.value ?: "N/A"
                    val posterUrl = posterMatch?.groups?.get(1)?.value ?: ""

                    // Update UI on the main thread
                    runOnUiThread {
                        movieTitle.text = title
                        movieYear.text = year

                        // Use Glide to load the poster image. Add Glide dependency to your build.gradle if you haven't.
                        Glide.with(this@MainActivity)
                            .load(posterUrl)
                            .into(moviePoster)
                    }
                }
            }

        })
    }
}