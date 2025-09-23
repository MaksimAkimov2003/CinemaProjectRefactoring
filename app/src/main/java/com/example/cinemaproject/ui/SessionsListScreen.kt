package com.example.cinemaproject.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.cinemaproject.data.FilmCatalog
import com.example.cinemaproject.data.TokenStorage
import com.example.cinemaproject.network.ApiProvider
import com.example.cinemaproject.network.Session
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import coil.request.ImageRequest
import coil.request.CachePolicy
import android.util.Log

@Composable
fun SessionsListScreen(
    tokenStorage: TokenStorage,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(true) }
    val sessions = remember { mutableStateOf(listOf<Session>()) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = tokenStorage.getAccessToken()
                val api = ApiProvider.getApi(context)
                val resp = api.getSessions(
                    authorization = token?.let { "Bearer $it" } ?: ""
                )
                sessions.value = resp.data
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(e.message ?: "Failed to load sessions")
            } finally {
                isLoading.value = false
            }
        }
    }

    if (isLoading.value) {
        CircularProgressIndicator()
        return
    }

    SnackbarHost(hostState = snackbarHostState)

    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(sessions.value) { s ->
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    val film = FilmCatalog.getInfo(s.filmId)
                    val request = ImageRequest.Builder(context)
                        .data(film?.imageUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build()
                    AsyncImage(
                        model = request,
                        contentDescription = film?.titleRu ?: s.filmId,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(Color(0xFFDDDDDD)),
                        error = ColorPainter(Color(0xFFDDDDDD)),
                        onError = {
                            Log.e("SessionsList", "Image load failed for ${film?.imageUrl}: ${it.result.throwable.message}")
                        }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    val time = try {
                        DateTimeFormatter.ofPattern("HH:mm").format(OffsetDateTime.parse(s.startAt))
                    } catch (e: Exception) { s.startAt }
                    androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                        Text(text = film?.titleRu ?: s.filmId)
                        Text(text = "Зал: ${s.hallId}  •  ${time}")
                    }
                }
            }
        }
    }
}

