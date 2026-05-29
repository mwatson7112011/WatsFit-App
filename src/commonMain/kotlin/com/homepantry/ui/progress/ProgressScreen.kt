package com.homepantry.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.HeartRateContext
import com.homepantry.domain.model.HeartRateLog
import com.homepantry.domain.model.WeightLog

class ProgressScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProgressScreenModel>()
        val state by screenModel.state.collectAsState()

        var showWeightDialog by remember { mutableStateOf(false) }
        var showHRDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Wellness Progress",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showWeightDialog = true }) { Icon(Icons.Default.Scale, "Log Weight") }
                        IconButton(onClick = { showHRDialog = true }) { Icon(Icons.Default.MonitorHeart, "Log HR") }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Weight logs", fontWeight = FontWeight.Bold) }
                    items(state.weights.reversed()) { log ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${log.weightLbs} lbs", fontWeight = FontWeight.Bold)
                                Text(log.date)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)); Text("Heart Rate logs", fontWeight = FontWeight.Bold) }
                    items(state.heartRates) { log ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${log.bpm} BPM", fontWeight = FontWeight.Bold)
                                Text("${log.date} ${log.time}")
                            }
                        }
                    }
                }
            }

            if (showWeightDialog) {
                LogWeightDialog(
                    onDismiss = { showWeightDialog = false },
                    onConfirm = { date, w, n -> screenModel.logWeight(date, w, n); showWeightDialog = false }
                )
            }
            if (showHRDialog) {
                LogHRDialog(
                    onDismiss = { showHRDialog = false },
                    onConfirm = { d, t, b, c, n -> screenModel.logHeartRate(d, t, b, c, n); showHRDialog = false }
                )
            }
        }
    }
}

@Composable
fun LogWeightDialog(onDismiss: () -> Unit, onConfirm: (String, Double, String?) -> Unit) {
    var weight by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Weight") },
        text = {
            OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (lbs)") })
        },
        confirmButton = {
            Button(onClick = { onConfirm("2024-05-28", weight.toDoubleOrNull() ?: 0.0, null) }) { Text("Log") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun LogHRDialog(onDismiss: () -> Unit, onConfirm: (String, String, Int, HeartRateContext, String?) -> Unit) {
    var bpm by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Heart Rate") },
        text = {
            OutlinedTextField(value = bpm, onValueChange = { bpm = it }, label = { Text("BPM") })
        },
        confirmButton = {
            Button(onClick = { onConfirm("2024-05-28", "08:00", bpm.toIntOrNull() ?: 70, HeartRateContext.RESTING, null) }) { Text("Log") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
