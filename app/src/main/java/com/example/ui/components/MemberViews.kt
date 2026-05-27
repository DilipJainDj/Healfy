package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.MessageEntity
import com.example.viewmodel.GymViewModel
import kotlinx.coroutines.delay

@Composable
fun MemberDashboardScreen(
    viewModel: GymViewModel
) {
    val weightHistory by viewModel.memberWeightHistory.collectAsState()
    val workoutProgress by viewModel.activeWorkoutProgress.collectAsState()
    val waterIntake by viewModel.waterIntakeMl.collectAsState()
    val proteinGrams by viewModel.proteinGrams.collectAsState()
    val messages by viewModel.messages.collectAsState()

    var chatText by remember { mutableStateOf("") }
    var simulatorTimeLeft by remember { mutableStateOf(45) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Chat with Trainer (Filter out messages corresponding to Coach Alex / Alex Rivera)
    val chatFlow = messages.filter {
        (it.senderId == 99 && it.receiverId == 1) || (it.senderId == 1 && it.receiverId == 99)
    }

    // Timer coroutine
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (simulatorTimeLeft > 0) {
                delay(1000)
                simulatorTimeLeft -= 1
            }
            if (simulatorTimeLeft == 0) {
                isTimerRunning = false
                viewModel.completeWorkoutSimulator()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Membership Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ElectricLime, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "PREMIUM ELITE MEMBER",
                            color = ElectricLime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Welcome back,\nAlex Rivera",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Consistency is paying off. Top 5% active.",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DAYS REMAINING", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("214", color = ElectricLime, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)

                    // Linear indicator bar
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.7f)
                                .background(ElectricLime)
                        )
                    }
                }
            }
        }

        // 2. Active Workout Simulator / Today's Focus
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TODAY'S WORKOUT TARGET", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("High-Intensity Power & Agility", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("$simulatorTimeLeft MINS", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Workout Steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkoutStepItem("Warmup", "10M", Modifier.weight(1f))
                    WorkoutStepItem("Lifts", "4 Sets", Modifier.weight(1f))
                    WorkoutStepItem("HIIT", "3 Rnds", Modifier.weight(1f))
                    WorkoutStepItem("Recovery", "5M", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulator controller button
                when (workoutProgress) {
                    null -> {
                        Button(
                            onClick = { viewModel.startWorkoutSimulator(); isTimerRunning = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Workout Session", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    "active" -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { isTimerRunning = !isTimerRunning },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) Color.DarkGray else ElectricLime),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isTimerRunning) "Pause Session" else "Resume Session", color = if (isTimerRunning) Color.White else Color.Black)
                            }
                            Button(
                                onClick = { viewModel.completeWorkoutSimulator(); isTimerRunning = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Complete Now", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    "completed" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ElectricLime.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("⚡ LEG DAY COMPLETED!", color = ElectricLime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Great hustle! Your activity data was successfully sent to Coach Alex.", color = Color.LightGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.resetWorkoutSimulator(); simulatorTimeLeft = 45 },
                                border = BorderStroke(1.dp, ElectricLime)
                            ) {
                                Text("Reset Session", color = ElectricLime, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Weight Progress Graph Module
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weight Trend Tracker", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("-2.4kg", color = ElectricLime, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("30 Days", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Custom drawn Line Chart
                CustomLineChart(
                    points = weightHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Week 1", color = Color.Gray, fontSize = 10.sp)
                    Text("Week 2", color = Color.Gray, fontSize = 10.sp)
                    Text("Week 3", color = Color.Gray, fontSize = 10.sp)
                    Text("Week 4", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }

        // 4. Fuel Your Body / Macro tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fuel & Hydration Level", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Water increment tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HYDRATION TARGET", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$waterIntake ml / 3000 ml", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.incrementWater() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, ElectricLime)
                    ) {
                        Text("+250ml", color = ElectricLime, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Protein increment tracker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PROTEIN TARGET", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$proteinGrams g / 150 g", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.incrementProtein() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, ElectricLime)
                    ) {
                        Text("+15g Protein", color = ElectricLime, fontSize = 12.sp)
                    }
                }
            }
        }

        // 5. Achievements grid
        Text("Achievements unlocked", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AchievementBadge("30 DAY STREAK", "🔥", Modifier.weight(1f))
            AchievementBadge("DUES SETTLED", "✉", Modifier.weight(1f))
            AchievementBadge("SPEED LIMIT", "⚡", Modifier.weight(1f))
            AchievementBadge("EARLY BIRD", "☼", Modifier.weight(1f))
        }

        // 6. Messaging with Coach Alex
        Spacer(modifier = Modifier.height(12.dp))
        Text("Live Message with Coach Alex", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatFlow.forEach { msg ->
                        val isMe = msg.senderId == 99
                        Column(
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = if (isMe) 8.dp else 0.dp,
                                            bottomEnd = if (isMe) 0.dp else 8.dp
                                        )
                                    )
                                    .background(if (isMe) ElectricLime else BorderGrey)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (isMe) Color.Black else Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = if (isMe) "You" else "Coach",
                                fontSize = 9.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatText,
                        onValueChange = { chatText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask Coach Alex...", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (chatText.isNotEmpty()) {
                                viewModel.sendMessage(chatText, isMemberSending = true)
                                chatText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = ElectricLime)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send text")
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutStepItem(label: String, valStr: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label.uppercase(), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(valStr, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AchievementBadge(title: String, symbol: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ElectricLime.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(symbol, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
