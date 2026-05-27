package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.db.MemberEntity
import com.example.db.MessageEntity
import com.example.viewmodel.GymViewModel

@Composable
fun TrainerDashboardScreen(
    viewModel: GymViewModel
) {
    val members by viewModel.members.collectAsState()
    val selectedMember by viewModel.selectedMember.collectAsState()
    val messages by viewModel.messages.collectAsState()

    var chatText by remember { mutableStateOf("") }
    var showAssignWorkoutSheet by remember { mutableStateOf(false) }
    var showAssignDietSheet by remember { mutableStateOf(false) }

    // Roster filter
    val clients = members.filter { it.role == "Member" && it.name != "Alex Rivera" }

    // Trainer Messages with Sarah Jenkins (virtually filter senderId/receiverId)
    val chatFlow = messages.filter {
        (it.senderId == 3 && it.receiverId == 1) || (it.senderId == 1 && it.receiverId == 3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TrainerStatCard("Active Clients", "24", modifier = Modifier.weight(1f))
            TrainerStatCard("Sessions Today", "8", modifier = Modifier.weight(1f))
            TrainerStatCard("Pending Alerts", "3", isError = true, modifier = Modifier.weight(1f))
        }

        // Assigned client roster
        Text("Assigned Members Directory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            clients.forEach { client ->
                val isSelected = selectedMember?.id == client.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectMember(if (isSelected) null else client) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ElectricLime.copy(alpha = 0.15f) else SurfaceGrey
                    ),
                    border = BorderStroke(1.dp, if (isSelected) ElectricLime else BorderGrey)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (client.avatarUrl.isNotEmpty()) {
                            AsyncImage(
                                model = client.avatarUrl,
                                contentDescription = client.name,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(ElectricLime, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    client.name.first().toString(),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(client.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(client.progressGoal, color = Color.Gray, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                client.membershipStatus.uppercase(),
                                color = if (client.membershipStatus == "Active") ElectricLime else ErrorRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${client.daysRemaining} days left", color = Color.Gray, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // Selected client drill-down detail sheet
        AnimatedVisibility(visible = selectedMember != null) {
            selectedMember?.let { client ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                    border = BorderStroke(1.dp, ElectricLime)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "MEMBER PROGRESS REVIEW",
                            color = ElectricLime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(client.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Goal: ${client.progressGoal} | Height: ${client.heightCm}cm | Weight: ${client.currentWeight}kg",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showAssignWorkoutSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricLime),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Assign Workout", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { showAssignDietSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Assign Diet", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Workout assigning mock details
                        if (showAssignWorkoutSheet) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("ASSIGNED WORKOUT TARGET", color = ElectricLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Hypertrophy Deadlift Core (45 Mins, 4 Sets)", color = Color.White, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { showAssignWorkoutSheet = false }) {
                                            Text("Acknowledge", color = ElectricLime, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Diet assigning details
                        if (showAssignDietSheet) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("ASSIGNED DIETARY TARGET", color = ElectricLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("2,400 Kcal Carb-Cut (High Protein: 45%)", color = Color.White, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { showAssignDietSheet = false }) {
                                            Text("Acknowledge", color = ElectricLime, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trainer Message Board (Sarah Jenkins thread)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Trainer Chat Hub (Sarah J. Thread)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatFlow.forEach { msg ->
                        val isCoach = msg.senderId == 1
                        Column(
                            horizontalAlignment = if (isCoach) Alignment.End else Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = if (isCoach) 8.dp else 0.dp,
                                            bottomEnd = if (isCoach) 0.dp else 8.dp
                                        )
                                    )
                                    .background(if (isCoach) ElectricLime else BorderGrey)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (isCoach) Color.Black else Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = if (isCoach) "You" else "Sarah Jenkins",
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
                        placeholder = { Text("Response to Sarah J...", color = Color.Gray, fontSize = 12.sp) },
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
                                viewModel.sendMessage(chatText, isMemberSending = false)
                                chatText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = ElectricLime)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send message")
                    }
                }
            }
        }

        // Upcoming Session Timeline
        Spacer(modifier = Modifier.height(8.dp))
        Text("Coach Schedule Timeline", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TrainerTimelineCard("14:00 Today", "Strength Lab Program Class", "Sarah Jenkins", true)
            TrainerTimelineCard("16:30 Today", "Leg Day Foundation Session", "Marcus Thorne", false)
        }
    }
}

@Composable
fun TrainerStatCard(
    label: String,
    value: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, if (isError) ErrorRed.copy(alpha = 0.4f) else BorderGrey)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label.uppercase(), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = if (isError) ErrorRed else ElectricLime, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TrainerTimelineCard(time: String, title: String, clientName: String, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, if (isActive) ElectricLime.copy(alpha = 0.4f) else BorderGrey)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .background(if (isActive) ElectricLime else Color.Gray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Client: $clientName", color = Color.Gray, fontSize = 11.sp)
            }
            Text(time, color = if (isActive) ElectricLime else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
