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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.MemberEntity
import com.example.db.PaymentEntity
import com.example.viewmodel.GymViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: GymViewModel
) {
    val members by viewModel.members.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    var showAddMemberForm by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newGoal by remember { mutableStateOf("Hypertrophy") }
    val membershipTypes = listOf("Premium Elite", "Performance", "Essential")
    var selectedType by remember { mutableStateOf(membershipTypes[0]) }

    var selectedInvoicePayment by remember { mutableStateOf<PaymentEntity?>(null) }

    // Roster filter algorithms
    val activeMembersList = members.filter { it.role == "Member" }
    val filteredMembers = activeMembersList.filter { member ->
        val queryMatch = member.name.contains(searchQuery, ignoreCase = true) || member.email.contains(searchQuery, ignoreCase = true)
        val statusMatch = when (statusFilter) {
            "All" -> true
            "Active" -> member.membershipStatus == "Active"
            "Expired" -> member.membershipStatus == "Expired"
            "Frozen" -> member.membershipStatus == "Frozen"
            else -> true
        }
        queryMatch && statusMatch
    }

    val activeCount = activeMembersList.count { it.membershipStatus == "Active" }
    val expiredCount = activeMembersList.count { it.membershipStatus == "Expired" }
    val activeRatio = if (activeMembersList.isNotEmpty()) activeCount.toFloat() / activeMembersList.size else 0f
    val expiredRatio = if (activeMembersList.isNotEmpty()) expiredCount.toFloat() / activeMembersList.size else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Cards Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("MONTHLY REVENUE", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$142,500", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    CustomBarChart(
                        heights = listOf(0.3f, 0.45f, 0.6f, 0.5f, 0.8f, 1.0f),
                        labels = listOf("M", "T", "W", "T", "F", "S"),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("MEMBERSHIP RATIO", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Active: $activeCount", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = activeRatio,
                        color = ElectricLime,
                        trackColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Expired: $expiredCount", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = expiredRatio,
                        color = ErrorRed,
                        trackColor = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // Live check-in occupancy card
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
                        Text("LIVE PEAK INDICATOR", color = ElectricLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("342 Present Today", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Peak surge: 17:00 - 19:30", color = Color.Gray, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(ElectricLime.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", color = ElectricLime, fontSize = 16.sp)
                }
            }
        }

        // Member search directory header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gym Member Directory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showAddMemberForm = !showAddMemberForm },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)
            ) {
                Text(if (showAddMemberForm) "Dismiss" else "Add Member", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        // Add Member visual form
        AnimatedVisibility(visible = showAddMemberForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                border = BorderStroke(1.dp, ElectricLime)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("REGISTER NEW MEMBER", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Full Name", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Email address", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Phone number", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = newGoal,
                        onValueChange = { newGoal = it },
                        label = { Text("Metric Goal (Hypertrophy / Fat Loss / Strength)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = BorderGrey,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Membership Type Choice rows
                    Text("Membership tier choice", color = Color.Gray, fontSize = 11.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        membershipTypes.forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricLime,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                                viewModel.addMember(newName, newEmail, newPhone, newGoal, selectedType)
                                newName = ""
                                newEmail = ""
                                newPhone = ""
                                showAddMemberForm = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register & Save to Room", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search directory input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by name or email...", color = Color.Gray, fontSize = 12.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricLime,
                unfocusedBorderColor = BorderGrey,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Status Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All", "Active", "Expired", "Frozen")
            filters.forEach { filter ->
                FilterChip(
                    selected = statusFilter == filter,
                    onClick = { viewModel.setStatusFilter(filter) },
                    label = { Text(filter, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricLime,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        // Directory list display
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            filteredMembers.forEach { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                    border = BorderStroke(1.dp, BorderGrey)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(member.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(member.email, color = Color.Gray, fontSize = 11.sp)
                                Text("Tier: ${member.membershipType}", color = Color.LightGray, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = when (member.membershipStatus) {
                                                "Active" -> ElectricLime.copy(alpha = 0.2f)
                                                "Frozen" -> Color.LightGray.copy(alpha = 0.2f)
                                                else -> ErrorRed.copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        member.membershipStatus.uppercase(),
                                        color = when (member.membershipStatus) {
                                            "Active" -> ElectricLime
                                            "Frozen" -> Color.LightGray
                                            else -> ErrorRed
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Admin member operations row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = { viewModel.updateMemberStatus(member, "Active") },
                                colors = ButtonDefaults.textButtonColors(contentColor = ElectricLime),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Renew", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { viewModel.updateMemberStatus(member, "Frozen") },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Freeze", fontSize = 11.sp)
                            }
                            TextButton(
                                onClick = { viewModel.deleteMember(member) },
                                colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Delete", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Recent Payments Transaction Logs
        Spacer(modifier = Modifier.height(8.dp))
        Text("Payments Transaction logs", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            payments.forEach { pay ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedInvoicePayment = pay },
                    colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                    border = BorderStroke(1.dp, if (selectedInvoicePayment?.id == pay.id) ElectricLime else BorderGrey)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pay.memberName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("TXID: ${pay.txId} | ${pay.payMethod}", color = Color.Gray, fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${String.format("%.2f", pay.amount)}", color = ElectricLime, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "CLICK FOR GST INVOICE",
                                color = ElectricLime.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Itemized GST Invoice sheet overlay (if selected)
        AnimatedVisibility(visible = selectedInvoicePayment != null) {
            selectedInvoicePayment?.let { pay ->
                val basePrice = pay.amount / 1.18
                val gstAmount = pay.amount - basePrice

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, ElectricLime)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("EXCELERATE", color = ElectricLime, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                Text("GST Registration: 06AAAEE1234F1Z9", color = Color.DarkGray, fontSize = 9.sp)
                            }
                            Text("GST INVOICE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = BorderGrey)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("BILLED TO:", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(pay.memberName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TRANS DATE:", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("May 23, 2026", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("INVOICE NO:", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(pay.txId, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("PAY METHOD:", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${pay.payMethod} Secure", color = Color.White, fontSize = 11.sp)
                            }
                        }

                        if (pay.upiTxId.isNotEmpty()) {
                            Text("Payment tracking ID: ${pay.upiTxId}", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }

                        Divider(color = BorderGrey, modifier = Modifier.padding(vertical = 4.dp))

                        // Itemized breakdown
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("1x Elite Membership Package", color = Color.LightGray, fontSize = 12.sp)
                            Text("$${String.format("%.2f", basePrice)}", color = Color.White, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST (18% integrated CGST/SGST)", color = Color.Gray, fontSize = 11.sp)
                            Text("$${String.format("%.2f", gstAmount)}", color = Color.LightGray, fontSize = 11.sp)
                        }

                        Divider(color = BorderGrey)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL PAID AMOUNT (NET)", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", pay.amount)}", color = ElectricLime, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Thank you for training with EXCELERATE. This is an electronically generated valid tax invoice receipt.",
                            color = Color.DarkGray,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { selectedInvoicePayment = null },
                            colors = ButtonDefaults.buttonColors(containerColor = BorderGrey),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close Invoice Preview", color = Color.White)
                        }
                    }
                }
            }
        }

        // Inventory Alerts low whey Warns
        Spacer(modifier = Modifier.height(8.dp))
        Text("Equipment & Consumables Inventory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InventoryAlertRow("Whey Protein ISO-100", "2 units left", isCritical = true)
                InventoryAlertRow("Sanitizing Wet Wipes", "45 units left", isCritical = false)
            }
        }
    }
}

@Composable
fun InventoryAlertRow(itemName: String, amt: String, isCritical: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isCritical) ErrorRed.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(itemName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(if (isCritical) "CRITICAL RESTOCK DUE" else "Optimal Stock", color = if (isCritical) ErrorRed else Color.Gray, fontSize = 9.sp)
        }
        Box(
            modifier = Modifier
                .background(if (isCritical) ErrorRed else ElectricLime, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(amt, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
