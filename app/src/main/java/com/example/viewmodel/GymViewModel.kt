package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class GymViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DataRepository.getInstance(application)

    // Roles: PUBLIC, ADMIN, TRAINER, MEMBER, RECEPTIONIST
    private val _currentRole = MutableStateFlow("PUBLIC")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // Members list flow
    val members: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Trainers list flow
    val trainers: StateFlow<List<MemberEntity>> = repository.allTrainers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payments flow
    val payments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance flow
    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages flow
    val messages: StateFlow<List<MessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected member for detail drill-down (Admin & Trainer Dashboard)
    private val _selectedMember = MutableStateFlow<MemberEntity?>(null)
    val selectedMember: StateFlow<MemberEntity?> = _selectedMember.asStateFlow()

    // For Member BMI and weight tracking
    private val _memberWeightHistory = MutableStateFlow(listOf(78.5, 78.0, 77.2, 76.9, 76.1))
    val memberWeightHistory: StateFlow<List<Double>> = _memberWeightHistory.asStateFlow()

    // For active workout simulator
    private val _activeWorkoutProgress = MutableStateFlow<String?>(null) // null = idle, "active", "completed"
    val activeWorkoutProgress: StateFlow<String?> = _activeWorkoutProgress.asStateFlow()

    // Water intake in Member dashboard
    private val _waterIntakeMl = MutableStateFlow(1200)
    val waterIntakeMl: StateFlow<Int> = _waterIntakeMl.asStateFlow()

    // Protein intake in grams
    private val _proteinGrams = MutableStateFlow(95)
    val proteinGrams: StateFlow<Int> = _proteinGrams.asStateFlow()

    // For search query in Admin Member Directory
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Member tab filter: "All", "Active", "Expired", "Frozen"
    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    init {
        // Seed database
        repository.seedIfEmpty()
    }

    fun setRole(role: String) {
        _currentRole.value = role
        // Deselect details upon switching roles
        _selectedMember.value = null
    }

    fun selectMember(member: MemberEntity?) {
        _selectedMember.value = member
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    fun addMember(name: String, email: String, phone: String, goal: String, type: String) {
        viewModelScope.launch {
            val newMember = MemberEntity(
                name = name,
                email = email,
                phone = phone,
                role = "Member",
                membershipType = type,
                membershipStatus = "Active",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 30L * 86400000L, // 30 days trial
                daysRemaining = 30,
                progressGoal = goal,
                currentWeight = 80.0,
                targetWeight = 75.0,
                heightCm = 175.0,
                avatarUrl = ""
            )
            repository.insertMember(newMember)
        }
    }

    fun updateMemberStatus(member: MemberEntity, status: String) {
        viewModelScope.launch {
            val updated = member.copy(
                membershipStatus = status,
                daysRemaining = if (status == "Active") 30 else if (status == "Expired") 0 else member.daysRemaining
            )
            repository.updateMember(updated)
            // Sync selection
            if (_selectedMember.value?.id == member.id) {
                _selectedMember.value = updated
            }
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
            if (_selectedMember.value?.id == member.id) {
                _selectedMember.value = null
            }
        }
    }

    fun checkInMember(memberId: Int, memberName: String, checkInType: String = "Manual") {
        viewModelScope.launch {
            val attendanceRecord = AttendanceEntity(
                memberId = memberId,
                memberName = memberName,
                timestamp = System.currentTimeMillis(),
                checkInType = checkInType
            )
            repository.insertAttendance(attendanceRecord)
        }
    }

    fun collectPayment(memberId: Int, memberName: String, amount: Double, method: String) {
        viewModelScope.launch {
            val invoiceId = "#LF-${(100000..999999).random()}"
            val stripeTx = if (method == "Stripe") "STR_${UUID.randomUUID().toString().take(10)}" else ""
            val upiTx = if (method == "UPI") "upi${(100000000..999999999).random()}@okaxis" else ""
            val newPayment = PaymentEntity(
                memberId = memberId,
                memberName = memberName,
                amount = amount,
                status = "Paid",
                date = System.currentTimeMillis(),
                txId = invoiceId,
                upiTxId = upiTx.ifEmpty { stripeTx },
                payMethod = method
            )
            repository.insertPayment(newPayment)
        }
    }

    fun sendMessage(text: String, isMemberSending: Boolean) {
        viewModelScope.launch {
            val message = if (isMemberSending) {
                MessageEntity(
                    senderId = 99, // Alex Rivera's virtual ID
                    senderName = "Alex Rivera",
                    receiverId = 1, // Coach Alex
                    receiverName = "Coach Alex",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                MessageEntity(
                    senderId = 1, // Coach Alex
                    senderName = "Coach Alex",
                    receiverId = _selectedMember.value?.id ?: 99,
                    receiverName = _selectedMember.value?.name ?: "Alex Rivera",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
            }
            repository.insertMessage(message)
        }
    }

    fun incrementWater() {
        _waterIntakeMl.value += 250
    }

    fun incrementProtein() {
        _proteinGrams.value += 15
    }

    fun startWorkoutSimulator() {
        _activeWorkoutProgress.value = "active"
    }

    fun completeWorkoutSimulator() {
        _activeWorkoutProgress.value = "completed"
        // Log activity indirectly by adding to weight or creating a message/event
    }

    fun resetWorkoutSimulator() {
        _activeWorkoutProgress.value = null
    }
}
