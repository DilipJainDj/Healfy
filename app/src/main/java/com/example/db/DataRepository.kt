package com.example.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataRepository private constructor(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "excelerate_gym_db"
    ).build()

    private val dao = db.gymDao()

    val allMembers: Flow<List<MemberEntity>> = dao.getAllMembers()
    val allTrainers: Flow<List<MemberEntity>> = dao.getAllTrainers()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
    val allAttendance: Flow<List<AttendanceEntity>> = dao.getAllAttendance()
    val allMessages: Flow<List<MessageEntity>> = dao.getAllMessages()

    suspend fun getMemberById(id: Int): MemberEntity? = dao.getMemberById(id)
    suspend fun getWorkoutPlan(memberId: Int): WorkoutPlanEntity? = dao.getWorkoutPlanForMember(memberId)
    suspend fun getDietPlan(memberId: Int): DietPlanEntity? = dao.getDietPlanForMember(memberId)

    suspend fun insertMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        dao.insertMember(member)
    }

    suspend fun updateMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        dao.updateMember(member)
    }

    suspend fun deleteMember(member: MemberEntity) = withContext(Dispatchers.IO) {
        dao.deleteMember(member)
    }

    suspend fun insertPayment(payment: PaymentEntity) = withContext(Dispatchers.IO) {
        dao.insertPayment(payment)
    }

    suspend fun insertAttendance(attendance: AttendanceEntity) = withContext(Dispatchers.IO) {
        dao.insertAttendance(attendance)
    }

    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity) = withContext(Dispatchers.IO) {
        dao.insertWorkoutPlan(plan)
    }

    suspend fun insertDietPlan(plan: DietPlanEntity) = withContext(Dispatchers.IO) {
        dao.insertDietPlan(plan)
    }

    suspend fun insertMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    // --- Seeding realistic data if DB is empty ---
    fun seedIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            val membersList = dao.getAllMembers().first()
            if (membersList.isEmpty()) {
                // Seed Coach
                val trainerId = dao.insertMember(
                    MemberEntity(
                        name = "Coach Alex",
                        email = "coach.alex@excelerate.com",
                        phone = "+1 555-0100",
                        role = "Trainer",
                        membershipType = "N/A",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 31536000000L,
                        endDate = System.currentTimeMillis() + 31536000000L,
                        daysRemaining = 365,
                        progressGoal = "Athletic Longevity",
                        currentWeight = 85.0,
                        targetWeight = 85.0,
                        heightCm = 184.0,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCJ2qHhnGqB4Bkg5maG1GH-F_xcJBk23x-1yephenTiJU2uPwsjgBU1Yoya8HodJP3jQuyPXXRKtU3hyIBgTvPiZva3m_RCdM-aUrpawsODE8wCN3dbxXPYmq6lxt2CNCULd08EY7GSuFhx25nv-pqqtrkaKPQMr5cha1LlTRc8yCHiSYapvbCfBJBwtZGXhgbCINmR_JBiN0pePLUvU1eNOtetEYZp2xV2VJeFXY4wui76l2sAA0KKkztTywc8UJP5Eoaq-St0OVc"
                    )
                ).toInt()

                // Seed Member Alex Rivera (The primary member dashboard profile)
                val alexId = dao.insertMember(
                    MemberEntity(
                        name = "Alex Rivera",
                        email = "alex.rivera@gmail.com",
                        phone = "+1 555-0199",
                        role = "Member",
                        membershipType = "Premium Elite",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 10000000000L,
                        endDate = System.currentTimeMillis() + 214 * 86400000L, // 214 days left
                        daysRemaining = 214,
                        progressGoal = "Strength & Agility",
                        currentWeight = 78.5,
                        targetWeight = 76.0,
                        heightCm = 178.0,
                        trainerId = trainerId,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBg8bXvCftO7Vn7L3Rdnc2yuUp8WmgPQkQnnXIX98y_J5jK5eAmzcV_LohAf-NSuHt2EYxWU8D0gxhJFxltxa6hBFuTN_vv6p1Izrf_b2gMCZGZYiDoFdF0724YVXKjFt1VQV2LOpmsKRMISkC9Is_CVzvyElG2JiAIA11YoV6utAwZqh92l209r0RdRHxCEAVNfo2IhM1MtznOkdaC_Nj4G2zZFZgzQG4zkHvjviMWuvHxbtOlmuiKVIotqWfGX282ynU9oRPRMP0"
                    )
                ).toInt()

                // Seed Member Sarah Jenkins
                dao.insertMember(
                    MemberEntity(
                        name = "Sarah Jenkins",
                        email = "sarah.j@athlete.com",
                        phone = "+1 555-0211",
                        role = "Member",
                        membershipType = "Performance Membership",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 60 * 86400000L,
                        endDate = System.currentTimeMillis() + 300 * 86400000L,
                        daysRemaining = 300,
                        progressGoal = "Hypertrophy",
                        currentWeight = 62.1,
                        targetWeight = 60.0,
                        heightCm = 168.0,
                        trainerId = trainerId,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDnHoxtM8jhW7IFHcKbV3-kMzmwHgo1pepoKZSbXxpHW3_arsSTKqWBKhpX7g1UPPl2UYMMFEEmuAbs4c73Bgeo6Kv_C4z5ae4YMeJtTn3kVbcD6Kp5_q6AgXvRotux7TVpvQwRYIWGWjxAxJpkvPMBLQeXT1fyR-T1VraOZUxGDW2fetz0ZEirjRTaT51KIIjMnxUEastr1N_KCvr74Xb0cwq9VDM4Mbr4DBlPlM4_IZP2HX7eo6liEwtIq4jFXgugdVCTIOihcG4"
                    )
                )

                // Seed Member Marcus Thorne
                dao.insertMember(
                    MemberEntity(
                        name = "Marcus Thorne",
                        email = "m.thorne@vortex.io",
                        phone = "+1 555-0222",
                        role = "Member",
                        membershipType = "Pro Elite",
                        membershipStatus = "Expired",
                        startDate = System.currentTimeMillis() - 365 * 86400000L,
                        endDate = System.currentTimeMillis() - 7 * 86400000L,
                        daysRemaining = 0,
                        progressGoal = "Fat Loss",
                        currentWeight = 94.2,
                        targetWeight = 85.0,
                        heightCm = 182.0,
                        trainerId = trainerId,
                        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAaSz6c7WCfVM8haXPx7FNDagO-POuZHJtIkQ62lBPol_4BJS67Yr2QZI3SU5hcK5FiEkDGtwxGQvITTZpMZcoWTIhjB_JQFCY7U3FlG_EwAV3WNLZePvWxFFE2o23zIIisXNyvts1AaX0XBUFyv_b8gfru_KX96lzY73Lbjd4_aiMstmE8l_yTtJoZXlbLiiEieeYYG92QjguXhez7gfodocdNWpY_tc5B0wwTM6iuviJ1DtHvcDpWG39DblJkkjPLFMDJrh62THU"
                    )
                )

                // Seed Jordan Davies
                dao.insertMember(
                    MemberEntity(
                        name = "Jordan Davies",
                        email = "j.davies@email.com",
                        phone = "+1 555-0301",
                        role = "Member",
                        membershipType = "Elite Annual",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 100 * 86400000L,
                        endDate = System.currentTimeMillis() + 265 * 86400000L,
                        daysRemaining = 265,
                        progressGoal = "Strength",
                        currentWeight = 88.0,
                        targetWeight = 92.0,
                        heightCm = 180.0,
                        trainerId = trainerId,
                        avatarUrl = ""
                    )
                )

                // Seed Maya Lopez
                dao.insertMember(
                    MemberEntity(
                        name = "Maya Lopez",
                        email = "m.lopez@email.com",
                        phone = "+1 555-0302",
                        role = "Member",
                        membershipType = "Pro Monthly",
                        membershipStatus = "Expired",
                        startDate = System.currentTimeMillis() - 40 * 86400000L,
                        endDate = System.currentTimeMillis() - 10 * 86400000L,
                        daysRemaining = 0,
                        progressGoal = "Endurance",
                        currentWeight = 55.4,
                        targetWeight = 56.0,
                        heightCm = 162.0,
                        trainerId = trainerId,
                        avatarUrl = ""
                    )
                )

                // Seed Sam Taylor
                dao.insertMember(
                    MemberEntity(
                        name = "Sam Taylor",
                        email = "s.taylor@email.com",
                        phone = "+1 555-0303",
                        role = "Member",
                        membershipType = "Corporate",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 15 * 86400000L,
                        endDate = System.currentTimeMillis() + 350 * 86400000L,
                        daysRemaining = 350,
                        progressGoal = "General Fitness",
                        currentWeight = 74.0,
                        targetWeight = 74.0,
                        heightCm = 175.0,
                        trainerId = trainerId,
                        avatarUrl = ""
                    )
                )

                // Seed Elena Rodriguez
                dao.insertMember(
                    MemberEntity(
                        name = "Elena Rodriguez",
                        email = "elena.rod@gmail.com",
                        phone = "+1 555-0304",
                        role = "Member",
                        membershipType = "Elite Annual",
                        membershipStatus = "Active",
                        startDate = System.currentTimeMillis() - 4 * 30 *  86400000L,
                        endDate = System.currentTimeMillis() + 8 * 30 * 86400000L,
                        daysRemaining = 240,
                        progressGoal = "Fat Loss",
                        currentWeight = 68.0,
                        targetWeight = 61.0,
                        heightCm = 169.0,
                        trainerId = trainerId,
                        avatarUrl = ""
                    )
                )

                // Seed Workout Plan for Alex Rivera
                dao.insertWorkoutPlan(
                    WorkoutPlanEntity(
                        memberId = alexId,
                        focusTitle = "High-Intensity Power & Agility",
                        durationMinutes = 45,
                        exercisesJson = "[Dynamic Warmup (10 min), Compound Lifts (4 sets), HIIT Circuit (3 rounds), Active Recovery (5 min)]"
                    )
                )

                // Seed Diet Plan for Alex Rivera
                dao.insertDietPlan(
                    DietPlanEntity(
                        memberId = alexId,
                        totalCalories = 2400,
                        proteinPercent = 40,
                        carbsPercent = 30,
                        fatsPercent = 30,
                        postWorkoutMeal = "Grilled Salmon & Quinoa Bowl",
                        snackMeal = "Greek Yogurt with Mixed Berries"
                    )
                )

                // Seed Payments
                dao.insertPayment(
                    PaymentEntity(
                        memberId = alexId,
                        memberName = "Alex Rivera",
                        amount = 1200.00,
                        status = "Paid",
                        date = System.currentTimeMillis() - 5 * 86400000L,
                        txId = "#LF-882941-A",
                        upiTxId = "UPI837429103847@okaxis",
                        payMethod = "UPI"
                    )
                )
                dao.insertPayment(
                    PaymentEntity(
                        memberId = alexId,
                        memberName = "Alex Rivera",
                        amount = 240.00,
                        status = "Pending",
                        date = System.currentTimeMillis() - 1 * 86400000L,
                        txId = "#LF-882939-B",
                        upiTxId = "",
                        payMethod = "Stripe"
                    )
                )
                dao.insertPayment(
                    PaymentEntity(
                        memberId = alexId,
                        memberName = "Alex Rivera",
                        amount = 599.00,
                        status = "Paid",
                        date = System.currentTimeMillis() - 25 * 86400000L,
                        txId = "#LF-882935-C",
                        upiTxId = "STR_981273948",
                        payMethod = "Stripe"
                    )
                )

                // Seed Attendance Logs
                dao.insertAttendance(
                    AttendanceEntity(
                        memberId = alexId,
                        memberName = "Alex Rivera",
                        timestamp = System.currentTimeMillis() - 12 * 3600000L,
                        checkInType = "QR Code"
                    )
                )
                dao.insertAttendance(
                    AttendanceEntity(
                        memberId = alexId,
                        memberName = "Alex Rivera",
                        timestamp = System.currentTimeMillis() - 36 * 3600000L,
                        checkInType = "QR Code"
                    )
                )

                // Seed Messages
                dao.insertMessage(
                    MessageEntity(
                        senderId = 3, // Sarah
                        senderName = "Sarah Jenkins",
                        receiverId = trainerId,
                        receiverName = "Coach Alex",
                        text = "Hey Coach, the deadlift weight felt a bit light today. Should I increase from 120kg?",
                        timestamp = System.currentTimeMillis() - 18 * 3600000L
                    )
                )
                dao.insertMessage(
                    MessageEntity(
                        senderId = trainerId,
                        senderName = "Coach Alex",
                        receiverId = 3,
                        receiverName = "Sarah Jenkins",
                        text = "Great job Sarah! Yes, let's step it up to 125kg for 3 reps, but keep the core extremely tight.",
                        timestamp = System.currentTimeMillis() - 12 * 3600000L
                    )
                )
                dao.insertMessage(
                    MessageEntity(
                        senderId = 4, // Marcus
                        senderName = "Marcus Thorne",
                        receiverId = trainerId,
                        receiverName = "Coach Alex",
                        text = "Sent the diet log for today. Felt good on protein targets.",
                        timestamp = System.currentTimeMillis() - 8 * 3600000L
                    )
                )
            }
        }
    }

    companion object {
        @Volatile private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository(context).also { instance = it }
            }
        }
    }
}
