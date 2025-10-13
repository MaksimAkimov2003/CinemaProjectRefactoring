package com.example.cinemaproject.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.cinemaproject.data.FilmCatalog
import com.example.cinemaproject.data.TokenStorage
import com.example.cinemaproject.network.ApiProvider
import com.example.cinemaproject.network.Session
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable


data class DayItem(val isoDate: String, val display: String)

fun generateDates(): List<DayItem> {
    val today = LocalDate.now()
    return (0..4).map { offset ->
        val d = today.plusDays(offset.toLong())
        val iso = d.toString() // YYYY-MM-DD
        val dow = when (d.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "понедельник"
            java.time.DayOfWeek.TUESDAY -> "вторник"
            java.time.DayOfWeek.WEDNESDAY -> "среда"
            java.time.DayOfWeek.THURSDAY -> "четверг"
            java.time.DayOfWeek.FRIDAY -> "пятница"
            java.time.DayOfWeek.SATURDAY -> "суббота"
            java.time.DayOfWeek.SUNDAY -> "воскресенье"
        }
        val displayDate = d.format(DateTimeFormatter.ofPattern("dd.MM"))
        val label = if (offset == 0) "Сегодня" else "$displayDate $dow"
        DayItem(iso, label)
    }
}

@Composable
fun SessionsListScreen(
    tokenStorage: TokenStorage,
    onOpenDetails: (sessionId: String, hallId: String) -> Unit = { _, _ -> },
    onOpenFilm: (filmId: String, title: String, imageUrl: String) -> Unit = { _, _, _ -> },
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(true) }
    val sessions = remember { mutableStateOf(listOf<Session>()) }

    val dates = remember { generateDates() }
    var selectedDate by remember { mutableStateOf(dates.first().isoDate) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedDate) {
        scope.launch {
            try {
                val token = tokenStorage.getAccessToken()
                val api = ApiProvider.getApi(context)
                val resp = api.getSessions(
                    authorization = token?.let { "Bearer $it" } ?: "",
                    date = selectedDate
                )
                sessions.value = resp.data
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(e.message ?: "Failed to load sessions")
            } finally {
                isLoading.value = false
            }
        }
    }

    Column {
        LazyRow(contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dates) { d ->
                FilterChip(
                    selected = d.isoDate == selectedDate,
                    onClick = { selectedDate = d.isoDate },
                    label = { Text(d.display) },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }

        if (isLoading.value) {
            CircularProgressIndicator()
            return@Column
        }

        SnackbarHost(hostState = snackbarHostState)

        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sessions.value) { s ->
                val film = FilmCatalog.getInfo(s.filmId)
                val title = film?.titleRu ?: s.filmId
                val imageUrl = film?.imageUrl ?: ""
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp)
                        .clickable { onOpenFilm(s.filmId, title, imageUrl) }
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        val request = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .crossfade(true)
                            .build()
                        AsyncImage(
                            model = request,
                            contentDescription = title,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = ColorPainter(Color(0xFFDDDDDD)),
                            error = ColorPainter(Color(0xFFDDDDDD)),
                            onError = {
                                Log.e("SessionsList", "Image load failed for $imageUrl: ${it.result.throwable.message}")
                            }
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        val time = try {
                            DateTimeFormatter.ofPattern("HH:mm").format(OffsetDateTime.parse(s.startAt))
                        } catch (e: Exception) { s.startAt }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title)
                            Text(text = "Зал: ${s.hallId}  •  ${time}")
                        }
                        androidx.compose.material3.TextButton(onClick = { onOpenDetails(s.id, s.hallId) }) {
                            Text("Выбрать места")
                        }
                    }
                }
            }
        }
    }
}