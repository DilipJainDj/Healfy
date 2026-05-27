package com.example.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {
    // --- Members ---
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Int): MemberEntity?

    @Query("SELECT * FROM members WHERE role = 'Trainer' ORDER BY name ASC")
    fun getAllTrainers(): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    // --- Payments ---
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    // --- Attendance ---
    @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    // --- Workout Plans ---
    @Query("SELECT * FROM workout_plans WHERE memberId = :memberId LIMIT 1")
    suspend fun getWorkoutPlanForMember(memberId: Int): WorkoutPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity): Long

    // --- Diet Plans ---
    @Query("SELECT * FROM diet_plans WHERE memberId = :memberId LIMIT 1")
    suspend fun getDietPlanForMember(memberId: Int): DietPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlan(plan: DietPlanEntity): Long

    // --- Messages ---
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long
}

@Database(
    entities = [
        MemberEntity::class,
        PaymentEntity::class,
        AttendanceEntity::class,
        WorkoutPlanEntity::class,
        DietPlanEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao
}
