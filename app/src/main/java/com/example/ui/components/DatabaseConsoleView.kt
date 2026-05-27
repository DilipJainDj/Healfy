package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DatabaseConsoleScreen() {
    var activeSubTab by remember { mutableStateOf("PostgreSQL Schema") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val subTabs = listOf("PostgreSQL Schema", "MongoDB Collection", "REST & GraphQL APIs", "ER Digs / Arch.")
            subTabs.forEach { tab ->
                FilterChip(
                    selected = activeSubTab == tab,
                    onClick = { activeSubTab = tab },
                    label = { Text(tab, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricLime,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Text("Dynamic Database Console", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (activeSubTab) {
                        "PostgreSQL Schema" -> PostgresSchemaText()
                        "MongoDB Collection" -> MongoSchemaText()
                        "REST & GraphQL APIs" -> ApiDocumentationText()
                        "ER Digs / Arch." -> ErDiagramArchText()
                    }
                }
            }
        }
    }
}

@Composable
fun PostgresSchemaText() {
    Text(
        text = """
-- =========================================================
--             LIFE FITNESS GYM (EXCELERATE)
--        PRODUCTION POSTGRESQL PLATFORM DATABASE SCHEMA
-- =========================================================

-- Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. ROLES & PERMISSIONS
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- Super Admin, Trainer, Member, Receptionist
    description TEXT
);

CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL, -- read:billing, write:sessions, edit:members
    description TEXT
);

CREATE TABLE role_permissions (
    role_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
    permission_id INTEGER REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY(role_id, permission_id)
);

-- 2. CORE USERS & MEMBERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    role_id INTEGER REFERENCES roles(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE members (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    membership_status VARCHAR(50) DEFAULT 'Active', -- Active, Expired, Frozen
    membership_type VARCHAR(100) NOT NULL, -- Premium Elite, Performance, Essential
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_remaining INTEGER GENERATED ALWAYS AS (end_date - CURRENT_DATE) STORED,
    height_cm NUMERIC(5,2),
    current_weight_kg NUMERIC(5,2),
    target_weight_kg NUMERIC(5,2),
    trainer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    avatar_url TEXT
);

-- Indices for rapid logins and status validations
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_members_status ON members(membership_status);

-- 3. BILLING & PAYMENTS
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id INTEGER REFERENCES members(id) ON DELETE RESTRICT,
    amount NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    payment_status VARCHAR(50) DEFAULT 'Paid', -- Paid, Pending, Overdue
    payment_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    billing_address TEXT
);

CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID REFERENCES payments(id) ON DELETE CASCADE,
    tx_tracking_id VARCHAR(120) UNIQUE NOT NULL, -- #LF invoice IDs
    upi_transaction_id VARCHAR(120),
    stripe_charge_id VARCHAR(120),
    razorpay_order_id VARCHAR(120),
    payment_method VARCHAR(50) NOT NULL, -- UPI, Credit Card, Razorpay, Stripe, Cash
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. ATTENDANCE TRAFFIC
CREATE TABLE attendance (
    id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
    check_in_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    check_in_type VARCHAR(50) DEFAULT 'QR Code' -- QR Code, RFID Scan, Manual
);

CREATE INDEX idx_attendance_time ON attendance(check_in_time DESC);

-- 5. PLANS & LOGS
CREATE TABLE workout_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
    training_focus VARCHAR(150) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    assigned_by UUID REFERENCES users(id),
    exercises JSONB NOT NULL, -- Rich Sets, Reps, Targets
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diet_plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
    daily_calories INTEGER NOT NULL,
    protein_ratio INTEGER NOT NULL,
    carbs_ratio INTEGER NOT NULL,
    fats_ratio INTEGER NOT NULL,
    post_workout_meals TEXT,
    snack_schedule TEXT,
    assigned_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE supplement_logs (
    id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
    supplement_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    start_date DATE,
    end_date DATE,
    notes TEXT
);

CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action_type VARCHAR(100) NOT NULL, -- Freeze, Renew, Registration
    notes TEXT,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
        """.trimIndent(),
        color = ElectricLime,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
}

@Composable
fun MongoSchemaText() {
    Text(
        text = """
// =========================================================
//            LIFE FITNESS GYM (EXCELERATE)
//       PRODUCTION MONGODB (NOSQL) DATA ARCHITECTURE
// =========================================================

// 1. Members Collection
{
  "_id": ObjectId("647a98b211f43ba9a10271b3"),
  "name": "Alex Rivera",
  "email": "alex.rivera@gmail.com",
  "phone": "+1 555-0199",
  "role": "Member",
  "membership": {
    "tier": "Premium Elite",
    "status": "Active",
    "start_date": ISODate("2026-02-23T00:00:00Z"),
    "end_date": ISODate("2026-09-23T00:00:00Z"),
    "remaining_days": 214
  },
  "fitness_goals": {
    "primary_focus": "Strength & Agility",
    "weight_goal": "76.0",
    "timeline_weeks": 12
  },
  "physical_assessments": [
    {
      "timestamp": ISODate("2026-05-23T00:00:00Z"),
      "weight_kg": 78.5,
      "height_cm": 178.0,
      "body_fat_percent": 12.4,
      "tape_measurements": {
        "chest_inches": 42.0,
        "waist_inches": 31.5,
        "arms_inches": 15.2,
        "legs_inches": 22.4
      }
    }
  ],
  "assigned_trainer_id": ObjectId("647a98a002bc453910382ae9"),
  "created_at": ISODate("2026-02-23T07:20:00Z")
}

// 2. Payments & Transactions Collection
{
  "_id": ObjectId("647b19cf83c21aa5c5ef82bb"),
  "member_id": ObjectId("647a98b211f43ba9a10271b3"),
  "memberName": "Alex Rivera",
  "amount_due": 1200.00,
  "gst_details": {
    "state_gst_percent": 18.00,
    "invoice_number": "#LF-882941-A",
    "gstin_registered": "06AAAEE1234F1Z9"
  },
  "payment": {
    "status": "Paid",
    "method": "UPI",
    "timestamp": ISODate("2026-05-18T10:45:00Z"),
    "charge_id": "upi837429103847@okaxis"
  }
}

// 3. WorkoutPlans Collection (Denormalized)
{
  "_id": ObjectId("647c21da8aef43b928cd7612"),
  "member_id": ObjectId("647a98b211f43ba9a10271b3"),
  "focus_title": "High-Intensity Power & Agility",
  "duration_mins": 45,
  "exercise_routines": [
    { "exercise": "Dynamic Warmup", "duration_mins": 10 },
    { "exercise": "Weighted Squat/Compound Lifts", "sets": 4, "reps": 8, "weight_kg": 105 },
    { "exercise": "Metabolic Ropes HIIT Circuit", "rounds": 3, "seconds_on": 45 },
    { "exercise": "Breathing Active Recovery", "duration_mins": 5 }
  ],
  "scheduled_days": ["MON", "WED", "FRI"]
}
        """.trimIndent(),
        color = SoftCyan,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
}

@Composable
fun ApiDocumentationText() {
    Text(
        text = """
// =========================================================
//            LIFE FITNESS GYM (EXCELERATE)
//           REST & GRAPHQL PLATFORM ENDPOINTS
// =========================================================

// --- 1. REST API DEFINITION ---

// POST /api/v1/auth/login
// Payload: { "email": "alex.rivera@gmail.com", "password": "..." }
// Response: { "token": "JWT_TOKEN", "role": "Member", "user": { ... } }

// GET /api/v1/members/all
// Response: [ { "id": 1, "name": "Sarah J.", "status": "Active" } ]

// POST /api/v1/billing/collect
// Payload: { "member_id": 3, "amount": 240, "method": "Stripe" }
// Response: { "status": "Paid", "invoice_no": "#LF-100234", "gst_charge": 36.6 }

// GET /api/v1/analytics/realtime-occupancy
// Response: { "present_now": 342, "peak_hour_target": "18:00" }


// --- 2. GRAPHQL API SCHEMA ---

type Member {
    id: ID!
    name: String!
    email: String!
    membershipStatus: String!
    daysRemaining: Int!
    progressGoal: String
    weightLogs: [WeightLog]
}

type Query {
    getMemberProfile(id: ID!): Member
    getRevenueMetrics(daysRange: Int!): RevenueReport
    getLiveDashboardStatistics: DashboardStats
}

type Mutation {
    createNewMember(name: String!, email: String!, tier: String!): Member
    freezeMembershipStatus(id: ID!, months: Int!): StatusResponse
    renewMembershipStatus(id: ID!): StatusResponse
}
        """.trimIndent(),
        color = Color.LightGray,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
}

@Composable
fun ErDiagramArchText() {
    Text(
        text = """
=========================================================
          EXCELERATE PRODUCTION ENTERPRISE SYSTEM
               ER-RELATIONSHIPS DIAGRAM
=========================================================

     +--------------+           1 : M           +--------------------+
     |    USERS     | ------------------------- |      MEMBERS       |
     | (Auth Roles) |                           |  (Tier, Statuses)  |
     +--------------+                           +--------------------+
            |                                             |
            | 1 : M                                       | 1 : M
            |                                             |
     +--------------+                             +--------------------+
     |  AUDIT LOGS  |                             |     ATTENDANCE     |
     |              |                             |    (QR Traffic)    |
     +--------------+                             +--------------------+
                                                          |
                                                          | 1 : M
                                                          |
                                                  +--------------------+
                                                  |    WORKOUT/DIET    |
                                                  |    (Assigned)      |
                                                  +--------------------+
                                                          |
                                                          | 1 : M
                                                          |
                                                  +--------------------+
                                                  |      PAYMENTS      |
                                                  | (UPI Secure charge)|
                                                  +--------------------+


DEPLOYMENT STEPS (DOCKER / SUPABASE):
1. Spawn Postgres / Mongo containers: `docker-compose up -d`
2. Sync migrations using Prisma ORM: `npx prisma db push`
3. Launch Next.js web ecosystem server: `npm run dev`
4. Connect Android BuildConfig endpoint variables to local service IP!
        """.trimIndent(),
        color = Color.Gray,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
}
