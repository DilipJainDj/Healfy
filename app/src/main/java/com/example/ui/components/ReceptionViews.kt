package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.AttendanceEntity
import com.example.db.MemberEntity
import com.example.viewmodel.GymViewModel

@Composable
fun ReceptionDashboardScreen(
    viewModel: GymViewModel
) {
    val members by viewModel.members.collectAsState()
    val attendance by viewModel.attendance.collectAsState()

    var checkInStatusMessage by remember { mutableStateOf<String?>(null) }
    var selectedCollectMember by remember { mutableStateOf<MemberEntity?>(null) }
    var collectAmountInput by remember { mutableStateOf("120") }
    var collectPayMethod by remember { mutableStateOf("UPI") }

    val activeMembers = members.filter { it.role == "Member" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Attendance Check-in Simulator Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, ElectricLime)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("QR / RFID MANUAL RECEPTION CHECK-IN", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select a member to simulate physical card tap or QR scan:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable row of members for quick checking in
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeMembers.forEach { member ->
                        Box(
                            modifier = Modifier
                                .background(BorderGrey, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.checkInMember(member.id, member.name, "QR Code")
                                    checkInStatusMessage = "Tap Success: checked in ${member.name}!"
                                }
                                .padding(12.dp)
                        ) {
                            Text(member.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                checkInStatusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElectricLime.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(msg, color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick register and Payment Collection
        Text("Payment Collection Register", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("BILL PARTICIPANT MEMBERSHIP FEES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Text("Choose Member receiving payment:", color = Color.White, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeMembers.forEach { member ->
                        val isSelected = selectedCollectMember?.id == member.id
                        Box(
                            modifier = Modifier
                                .background(if (isSelected) ElectricLime else BorderGrey, RoundedCornerShape(8.dp))
                                .clickable { selectedCollectMember = member }
                                .padding(10.dp)
                        ) {
                            Text(member.name, color = if (isSelected) Color.Black else Color.White, fontSize = 12.sp)
                        }
                    }
                }

                selectedCollectMember?.let { member ->
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = collectAmountInput,
                        onValueChange = { collectAmountInput = it },
                        label = { Text("Billing Fees Amount ($)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Pay Method filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("UPI", "Stripe", "Cash", "Razorpay").forEach { method ->
                            FilterChip(
                                selected = collectPayMethod == method,
                                onClick = { collectPayMethod = method },
                                label = { Text(method, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricLime,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val amt = collectAmountInput.toDoubleOrNull() ?: 120.0
                            viewModel.collectPayment(member.id, member.name, amt, collectPayMethod)
                            selectedCollectMember = null
                            checkInStatusMessage = "Successfully billed $${amt} for ${member.name} via ${collectPayMethod}!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Collection", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live check-in flow list
        Spacer(modifier = Modifier.height(8.dp))
        Text("Live Daily Attendance feed", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (attendance.isEmpty()) {
                Text("No check-ins logged yet today.", color = Color.Gray, fontSize = 12.sp)
            } else {
                attendance.take(5).forEach { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                        border = BorderStroke(1.dp, BorderGrey)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(ElectricLime, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(log.memberName.first().toString(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(log.memberName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Logged via ${log.checkInType}", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                            Text("SUCCESS", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
