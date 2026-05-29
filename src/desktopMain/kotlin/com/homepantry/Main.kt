package com.homepantry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.homepantry.data.repository.RecipeRepository
import com.homepantry.data.repository.WorkoutRepository
import com.homepantry.data.seed.loadSeedData
import com.homepantry.di.commonModule
import com.homepantry.di.platformModule
import kotlinx.coroutines.delay
import org.koin.core.context.startKoin

fun main() {
    val koinApp = startKoin {
        modules(commonModule, platformModule)
    }
    val koin = koinApp.koin

    val recipeRepo = koin.get<RecipeRepository>()
    val workoutRepo = koin.get<WorkoutRepository>()
    
    loadSeedData(recipeRepo, workoutRepo) { fileName ->
        try {
            Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)?.bufferedReader()?.use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    application {
        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "WatsFit",
                state = rememberWindowState(
                    position = WindowPosition(Alignment.Center),
                    width = 500.dp,
                    height = 360.dp
                ),
                undecorated = true,
                resizable = false
            ) {
                LaunchedEffect(Unit) {
                    delay(2500)
                    showSplash = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0D1B2A))
                        .border(1.dp, Color(0xFF6B8F4E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource("watsfit_logo.png"),
                            contentDescription = "WatsFit Logo",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "WatsFit",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Get Healthy, Get Fit, with WatsFit",
                            fontSize = 18.sp,
                            color = Color(0xFF6B8F4E),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            color = Color(0xFF2A7B9B),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        } else {
            Window(
                onCloseRequest = ::exitApplication,
                title = "WatsFit",
                icon = painterResource("watsfit_logo.png"),
                state = rememberWindowState(width = 1024.dp, height = 768.dp)
            ) {
                App()
            }
        }
    }
}
