package com.example.cinemaproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddReviewScreen(
    filmId: String,
    title: String,
    onBack: () -> Unit = {}
) {
    val impressions = remember { mutableStateOf("") }
    val rating = remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = title)
        OutlinedTextField(
            value = impressions.value,
            onValueChange = { impressions.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ваши впечатления (опционально)") }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { i ->
                IconButton(onClick = { rating.value = i }) {
                    if (rating.value >= i) {
                        Icon(Icons.Filled.Star, contentDescription = "$i")
                    } else {
                        Icon(Icons.Outlined.Star, contentDescription = "$i")
                    }
                }
            }
            Text(text = "Оценка: ${rating.value}")
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                com.example.cinemaproject.ui.rating = 3.7
                onBack()
            }) { Text("Сохранить") }
            Button(onClick = onBack) { Text("Отмена") }
        }
    }
}


