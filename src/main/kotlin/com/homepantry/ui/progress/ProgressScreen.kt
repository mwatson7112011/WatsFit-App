package com.homepantry.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.homepantry.domain.model.HeartRateContext
import com.homepantry.domain.model.HeartRateLog
import com.homepantry.domain.model.WeightLog
import com.homepantry.ui.theme.AlertCoral
import com.homepantry.ui.theme.SecondaryDark
import com.homepantry.ui.theme.SuccessGreen
import com.homepantry.ui.theme.WarningGold
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class ProgressScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProgressScreenModel>()
        val state by screenModel.state.collectAsState()

        var showWeightDialog by remember { mutableStateOf(false) }
        var showHeartRateDialog by remember { mutableStateOf(false) }

        var selectedTab by remember { mutableStateOf(0) } // 0: Charts & Stats, 1: Log History

        LaunchedEffect(Unit) {
            screenModel.loadData()
        }

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wellness Progress",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Track weight loss and heart rate trends over time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showHeartRateDialog = true }) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = "Log HR")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Heart Rate")
                        }

                        Button(onClick = { showWeightDialog = true }) {
                            Icon(Icons.Default.Scale, contentDescription = "Log Weight")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Weight")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Dashboard Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val currentWeight = state.latestWeight?.weightLbs ?: 0.0
                    val startingWeight = state.firstWeight?.weightLbs ?: 0.0
                    val diff = currentWeight - startingWeight
                    val weightChangeText = if (diff == 0.0) {
                        "No change"
                    } else if (diff > 0) {
                        "+${"%.1f".format(diff)} lbs"
                    } else {
                        "${"%.1f".format(diff)} lbs"
                    }

                    StatCard(
                        icon = Icons.Default.Scale,
                        title = "Current Weight",
                        value = if (currentWeight > 0.0) "$currentWeight lbs" else "--",
                        subtitle = "Target: ${state.targetWeight} lbs",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        icon = Icons.Default.TrendingDown,
                        title = "Total Progress",
                        value = if (startingWeight > 0.0) weightChangeText else "--",
                        subtitle = "Since starting log",
                        modifier = Modifier.weight(1f),
                        valueColor = if (diff < 0.0) SuccessGreen else if (diff > 0.0) AlertCoral else Color.Unspecified
                    )

                    StatCard(
                        icon = Icons.Default.MonitorHeart,
                        title = "Avg Resting HR",
                        value = if (state.avgRestingHeartRate != null) "${state.avgRestingHeartRate!!.toInt()} BPM" else "--",
                        subtitle = "Past 30 days",
                        modifier = Modifier.weight(1f),
                        valueColor = SecondaryDark
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation tabs for subviews
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Trend Charts", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Data Log Logs", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left Chart: Weight Trend (Canvas)
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Weight Trend (lbs)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (state.weights.size < 2) {
                                        Text(
                                            "Log weight at least twice to see trend chart",
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        WeightLineChart(
                                            weights = state.weights,
                                            targetWeight = state.targetWeight
                                        )
                                    }
                                }
                            }
                        }

                        // Right Chart: Resting Heart Rate
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Resting Heart Rate (BPM)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val restingRates = state.heartRates
                                        .filter { it.context == HeartRateContext.RESTING }
                                        .sortedBy { it.date }
                                    if (restingRates.size < 2) {
                                        Text(
                                            "Log resting heart rate at least twice to see trend chart",
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        HeartRateChart(logs = restingRates)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left List: Weight History logs
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Weight Log History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.weights.reversed()) { log ->
                                    HistoryLogCard(
                                        title = "${log.weightLbs} lbs",
                                        subtitle = log.date,
                                        notes = log.notes,
                                        onDelete = { screenModel.deleteWeight(log.id) }
                                    )
                                }
                            }
                        }

                        // Right List: Heart Rate History logs
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            Text(
                                text = "Heart Rate History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.heartRates) { log ->
                                    HistoryLogCard(
                                        title = "${log.bpm} BPM",
                                        subtitle = "${log.date} ${log.time} (${log.context.displayName})",
                                        notes = log.notes,
                                        onDelete = { screenModel.deleteHeartRate(log.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showWeightDialog) {
                LogWeightDialog(
                    onDismiss = { showWeightDialog = false },
                    onConfirm = { date, weight, notes ->
                        screenModel.logWeight(date, weight, notes)
                        showWeightDialog = false
                    }
                )
            }

            if (showHeartRateDialog) {
                LogHeartRateDialog(
                    onDismiss = { showHeartRateDialog = false },
                    onConfirm = { date, time, bpm, context, notes ->
                        screenModel.logHeartRate(date, time, bpm, context, notes)
                        showHeartRateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun HistoryLogCard(
    title: String,
    subtitle: String,
    notes: String?,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!notes.isNullOrBlank()) {
                    Text(
                        notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun WeightLineChart(
    weights: List<WeightLog>,
    targetWeight: Double
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val maxVal = maxOf(weights.maxOf { it.weightLbs }, targetWeight) + 5
        val minVal = minOf(weights.minOf { it.weightLbs }, targetWeight) - 5
        val valueRange = maxVal - minVal

        val points = weights.mapIndexed { idx, log ->
            val x = (idx.toFloat() / (weights.size - 1)) * (width - 40f) + 20f
            val y = height - (((log.weightLbs.toFloat() - minVal.toFloat()) / valueRange.toFloat()) * (height - 60f) + 30f)
            Offset(x, y)
        }

        // Draw Target Line
        val targetY = height - (((targetWeight.toFloat() - minVal.toFloat()) / valueRange.toFloat()) * (height - 60f) + 30f)
        drawLine(
            color = AlertCoral,
            start = Offset(0f, targetY),
            end = Offset(width, targetY),
            strokeWidth = 2f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        // Draw Line connecting logs
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }
        drawPath(path = path, color = primaryColor, style = Stroke(width = 4f))

        // Draw dots
        points.forEach { point ->
            drawCircle(color = primaryColor, radius = 6f, center = point)
        }
    }
}

@Composable
fun HeartRateChart(logs: List<HeartRateLog>) {
    val secondaryColor = SecondaryDark
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val maxVal = (logs.maxOfOrNull { it.bpm } ?: 100) + 10
        val minVal = (logs.minOfOrNull { it.bpm } ?: 50) - 10
        val valueRange = maxVal - minVal

        val points = logs.mapIndexed { idx, log ->
            val x = (idx.toFloat() / (logs.size - 1)) * (width - 40f) + 20f
            val y = height - (((log.bpm.toFloat() - minVal.toFloat()) / valueRange.toFloat()) * (height - 60f) + 30f)
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }
        drawPath(path = path, color = secondaryColor, style = Stroke(width = 4f))

        points.forEach { point ->
            drawCircle(color = secondaryColor, radius = 6f, center = point)
        }
    }
}

@Composable
fun LogWeightDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?) -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
    var date by remember { mutableStateOf(today) }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Weight") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (lbs)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weight.toDoubleOrNull() ?: 0.0
                    onConfirm(date, w, notes.takeIf { it.isNotBlank() })
                },
                enabled = weight.isNotBlank()
            ) {
                Text("Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LogHeartRateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, HeartRateContext, String?) -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
    var date by remember { mutableStateOf(today) }
    var time by remember { mutableStateOf("08:00") }
    var bpm by remember { mutableStateOf("") }
    var context by remember { mutableStateOf(HeartRateContext.RESTING) }
    var notes by remember { mutableStateOf("") }

    var contextExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Heart Rate") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (HH:MM)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bpm,
                    onValueChange = { bpm = it },
                    label = { Text("BPM") },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = context.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Context") },
                        trailingIcon = {
                            IconButton(onClick = { contextExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Context")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = contextExpanded,
                        onDismissRequest = { contextExpanded = false }
                    ) {
                        HeartRateContext.entries.forEach { ctx ->
                            DropdownMenuItem(
                                text = { Text(ctx.displayName) },
                                onClick = {
                                    context = ctx
                                    contextExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rate = bpm.toIntOrNull() ?: 70
                    onConfirm(date, time, rate, context, notes.takeIf { it.isNotBlank() })
                },
                enabled = bpm.isNotBlank()
            ) {
                Text("Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
