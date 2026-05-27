package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GymViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GymAppMainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymAppMainScreen() {
    val viewModel: GymViewModel = viewModel()
    val currentRole by viewModel.currentRole.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreyBg),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE60F172A))
                    .statusBarsPadding()
            ) {
                // Main Header Title Line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EXCELERATE",
                            color = ElectricLime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Life Fitness Group Centers",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Active badge
                    Box(
                        modifier = Modifier
                            .background(ElectricLime.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DEMO ACTIVE",
                            color = ElectricLime,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Interactive Quick Swapper Selector Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val roles = listOf(
                        "PUBLIC" to "Public View",
                        "MEMBER" to "Member App",
                        "TRAINER" to "Trainer Desk",
                        "ADMIN" to "Admin Suite",
                        "RECEPTIONIST" to "Reception Desk",
                        "DATABASE" to "Database Console"
                    )

                    roles.forEach { (roleId, label) ->
                        val isSelected = currentRole == roleId
                        AssistChip(
                            onClick = { viewModel.setRole(roleId) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) ElectricLime else SurfaceGrey
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ElectricLime else Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGreyBg)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                // Load active layout dynamically depending on role selection!
                when (currentRole) {
                    "PUBLIC" -> WebsiteView(onJoinNowClick = { viewModel.setRole("MEMBER") })
                    "MEMBER" -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            MemberDashboardScreen(viewModel = viewModel)
                        }
                    }
                    "TRAINER" -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            TrainerDashboardScreen(viewModel = viewModel)
                        }
                    }
                    "ADMIN" -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            AdminDashboardScreen(viewModel = viewModel)
                        }
                    }
                    "RECEPTIONIST" -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            ReceptionDashboardScreen(viewModel = viewModel)
                        }
                    }
                    "DATABASE" -> DatabaseConsoleScreen()
                }
            }
        }
    }
}
