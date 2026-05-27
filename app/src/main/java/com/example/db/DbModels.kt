package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // "Member", "Trainer", "Admin", "Receptionist"
    val membershipType: String, // "Premium Elite", "Performance", "Essential"
    val membershipStatus: String, // "Active", "Expired", "Frozen"
    val startDate: Long,
    val endDate: Long,
    val daysRemaining: Int,
    val progressGoal: String, // "Hypertrophy", "Fat Loss", "Strength", "Endurance"
    val currentWeight: Double,
    val targetWeight: Double,
    val heightCm: Double,
    val trainerId: Int = 0,
    val avatarUrl: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val memberName: String,
    val amount: Double,
    val status: String, // "Paid", "Pending", "Due"
    val date: Long,
    val txId: String,
    val upiTxId: String = "",
    val payMethod: String // "UPI", "Stripe", "Razorpay", "Cash"
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val memberName: String,
    val timestamp: Long,
    val checkInType: String // "QR Code", "RFID", "Manual"
)

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val focusTitle: String,
    val durationMinutes: Int,
    val exercisesJson: String // Serialized dynamic sets/reps
)

@Entity(tableName = "diet_plans")
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val totalCalories: Int,
    val proteinPercent: Int,
    val carbsPercent: Int,
    val fatsPercent: Int,
    val postWorkoutMeal: String,
    val snackMeal: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: Int,
    val senderName: String,
    val receiverId: Int,
    val receiverName: String,
    val text: String,
    val timestamp: Long
)
