package com.example.cinemaproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.cinemaproject.data.TokenStorage
import com.example.cinemaproject.network.ApiProvider
import com.example.cinemaproject.network.HallPlan
import com.example.cinemaproject.network.Ticket
import kotlinx.coroutines.launch

@Composable
fun SessionDetailsScreen(
    tokenStorage: TokenStorage,
    sessionId: String,
    hallId: String,
    onBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val isLoading = remember(sessionId, hallId) { mutableStateOf(true) }
    val hallPlan = remember(sessionId, hallId) { mutableStateOf<HallPlan?>(null) }
    val tickets = remember(sessionId, hallId) { mutableStateOf<List<Ticket>>(emptyList()) }

    LaunchedEffect(sessionId, hallId) {
        scope.launch {
            try {
                val token = tokenStorage.getAccessToken()
                val api = ApiProvider.getApi(context)
                val plan = api.getHallPlan(authorization = token?.let { "Bearer $it" } ?: "", hallId = hallId)
                val ticketList = api.getSessionTickets(authorization = token?.let { "Bearer $it" } ?: "", sessionId = sessionId, status = null)
                hallPlan.value = plan
                tickets.value = ticketList
            } catch (_: Exception) {
            } finally {
                isLoading.value = false
            }
        }
    }

    if (isLoading.value) {
        CircularProgressIndicator()
        return
    }

    val plan = hallPlan.value ?: run {
        Column(modifier = Modifier.padding(12.dp)) { Text(text = "Нет данных по залу") }
        return
    }
    val seatIdToStatus = tickets.value.associateBy { it.seatId }.mapValues { it.value.status }
    val seatIdToSeat = remember(plan) { plan.seats.associateBy { it.id } }
    val categoryIdToPrice = remember(plan) { plan.categories.associate { it.id to it.priceCents } }
    val selectedSeatIds = remember(sessionId, hallId) { mutableStateOf<Set<String>>(emptySet()) }

    // Group by row for variable seat counts per row
    val seatsByRow = plan.seats.groupBy { it.row }.mapValues { it.value.sortedBy { s -> s.number } }.toSortedMap()

    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(24.dp).background(Color(0xFF3A2C5C)),
            contentAlignment = Alignment.Center
        ) { Text(text = "ЭКРАН", color = Color(0xFFBCA7FF), style = MaterialTheme.typography.labelLarge) }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(seatsByRow.entries.toList()) { (rowNum, rowSeats) ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rowNum.toString(),
                        modifier = Modifier.padding(end = 8.dp).size(width = 24.dp, height = 24.dp),
                        color = Color(0xFFBCA7FF), textAlign = TextAlign.Center
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                        items(rowSeats) { seat ->
                            val status = seatIdToStatus[seat.id] ?: "AVAILABLE"
                            val isAvailable = status !in listOf("SOLD", "RESERVED", "CANCELLED")
                            val isSelected = selectedSeatIds.value.contains(seat.id)
                            val color = when {
                                !isAvailable -> Color(0xFF6B5C8E)
                                isSelected -> Color(0xFF2ECC71)
                                else -> Color(0xFF8B6BE8)
                            }
                            val idx = rowSeats.indexOf(seat)
                            val mid = rowSeats.size / 2
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (idx == mid) Spacer(modifier = Modifier.size(20.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color, shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .border(1.dp, Color(0xFF2C2344), shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                        .let { m -> if (isAvailable) m.clickable {
                                            selectedSeatIds.value = if (isSelected) selectedSeatIds.value - seat.id else selectedSeatIds.value + seat.id
                                        } else m },
                                    contentAlignment = Alignment.Center
                                ) { Text(text = seat.number.toString(), color = Color.White, style = MaterialTheme.typography.labelMedium) }
                            }
                        }
                    }
                    Text(
                        text = rowNum.toString(),
                        modifier = Modifier.padding(start = 8.dp).size(width = 24.dp, height = 24.dp),
                        color = Color(0xFFBCA7FF), textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(16.dp).background(Color(0xFF8B6BE8)))
            Text(text = "Свободно", color = Color(0xFFBCA7FF))
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier.size(16.dp).background(Color(0xFF6B5C8E)))
            Text(text = "Занято", color = Color(0xFFBCA7FF))
        }

        if (selectedSeatIds.value.isNotEmpty()) {
            Spacer(modifier = Modifier.size(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(selectedSeatIds.value.toList()) { seatId ->
                    val seat = seatIdToSeat[seatId] ?: return@items
                    val priceRub = ((categoryIdToPrice[seat.categoryId] ?: 0) / 100)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1F58))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .widthIn(min = 220.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF2ECC71), shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                            ) {}
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(text = "Ряд ${seat.row}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Место ${seat.number}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text(text = "$priceRub ₽", color = Color(0xFFBCA7FF), style = MaterialTheme.typography.labelMedium)
                            }
                            Text(
                                text = "✕",
                                color = Color(0xFFBCA7FF),
                                modifier = Modifier.clickable { selectedSeatIds.value = selectedSeatIds.value - seatId }
                            )
                        }
                    }
                }
            }
        }
    }
}


