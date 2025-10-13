package com.example.cinemaproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.google.gson.Gson
import androidx.compose.ui.platform.LocalContext

@Composable
fun FilmDetailsScreen(filmId: String, title: String, imageUrl: String) {
    val isLoading = remember(filmId, imageUrl) { mutableStateOf(true) }
    val reviews = remember(filmId) { mutableStateOf(listOf<Review>()) }
    val context = LocalContext.current

    LaunchedEffect(filmId, imageUrl) {
        kotlinx.coroutines.delay(3000)
        // Load mock reviews from assets
        try {
            val json = context.assets.open("mocks/reviews_default.json").bufferedReader().use { it.readText() }
            val parsed = Gson().fromJson(json, Array<Review>::class.java).toList()
            reviews.value = parsed
        } catch (_: Exception) {
            reviews.value = emptyList()
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            loading = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            error = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Image failed to load", color = Color.Black)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        // Fixed rating = 4
        Text(text = "Рейтинг: 4/5", style = MaterialTheme.typography.bodyLarge, color = Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(reviews.value) { r ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = r.author, style = MaterialTheme.typography.titleSmall, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = r.text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = r.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

data class Review(
    val author: String,
    val text: String,
    val date: String,
)


