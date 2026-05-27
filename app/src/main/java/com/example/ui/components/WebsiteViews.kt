package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun WebsiteView(
    onJoinNowClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Home") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreyBg)
    ) {
        // Website Sub-Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(SurfaceGrey)
                .padding(vertical = 4.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val tabs = listOf("Home", "About Us", "Classes", "Diet Plans", "Transformations", "FAQ & Contact")
            tabs.forEach { tab ->
                TextButton(
                    onClick = { selectedTab = tab }
                ) {
                    Text(
                        text = tab,
                        color = if (selectedTab == tab) ElectricLime else Color.White,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            when (selectedTab) {
                "Home" -> WebHomeScreen(onJoinNowClick, onExploreClasses = { selectedTab = "Classes" })
                "About Us" -> WebAboutScreen()
                "Classes" -> WebClassesScreen()
                "Diet Plans" -> WebDietDetailsScreen()
                "Transformations" -> WebTransformationsScreen()
                "FAQ & Contact" -> WebContactScreen()
            }

            WebFooter()
        }
    }
}

@Composable
fun WebHomeScreen(
    onJoinNowClick: () -> Unit,
    onExploreClasses: () -> Unit
) {
    // 1. Hero Section
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuA-mviUSLLkz6iv_e0i5Rc9qpKKKgeZRRrigszA_mW7mbiZoi66wnxiBvnwsLTLuiC8Brv3NhE7K8vohiQ3r2P2Ngmdezazu9yoO-RQDiJQODl5-fnt6EKh1d4ap9BvoWi6BPGxBYyUlh4cY1OS3l5nK9RYAW6j_nXOXJIXBSFPgoRbjVoF8LwitpB000IlBWaug-LNVr8uiWhTR0QEnrNXZhTrqCIKw5_56lAnfOUqsrKrn4de2DZKBWamiZoq9oWFE-DeWaZLFcQ",
            contentDescription = "Hero Shot Gym Floor",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "START YOUR TRANSFORMATION",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unleash peak potential in an elite athletic laboratory. Precision coaching meets modern technology.",
                color = Color.LightGray,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onJoinNowClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)
                ) {
                    Text("Join Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onExploreClasses,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Text("Classes", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }

    // 2. Gym Statistics Bento Card Info
    Spacer(modifier = Modifier.height(16.dp))
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "ENGINEERED FOR EXCELLENCE",
            color = ElectricLime,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "We combine precise bio-mechanics with elite trainers to ensure results translate dramatically fast.",
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(value = "12K+", title = "Members", modifier = Modifier.weight(1f))
            StatBox(value = "50+", title = "Trainers", modifier = Modifier.weight(1f))
            StatBox(value = "100+", title = "Machines", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "MEMBER TESTIMONIALS",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TestimonialCard(
            quote = "\"The environment is unlike anything else. Trainers actually understand bio-mechanics. I hit PRs I never thought possible.\"",
            name = "Sarah Jenkins",
            membership = "Performance Elite Club"
        )
        Spacer(modifier = Modifier.height(8.dp))
        TestimonialCard(
            quote = "\"Super clean, high-tech equipment. The dedicated recovery zone alone justifies the elite membership. You train like an actual athlete.\"",
            name = "Marcus Thorne",
            membership = "Pro Elite Member"
        )

        Spacer(modifier = Modifier.height(24.dp))
        BmiCalculatorWidget()
    }
}

@Composable
fun StatBox(value: String, title: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = ElectricLime, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TestimonialCard(quote: String, name: String, membership: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey.copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(quote, color = Color.LightGray, fontSize = 12.sp, fontStyle = FontStyle.Italic, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(ElectricLime, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().toString(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(membership, color = Color.Gray, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun BmiCalculatorWidget() {
    var heightInput by remember { mutableStateOf("175") }
    var weightInput by remember { mutableStateOf("70") }
    var bmiResult by remember { mutableStateOf<Double?>(null) }
    var bmiCategory by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("HEALTH & BMI CALCULATOR", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Instantly see your healthy metrics & goals", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("Height (cm)", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = BorderGrey,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = BorderGrey,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val h = heightInput.toDoubleOrNull() ?: 1.0
                    val w = weightInput.toDoubleOrNull() ?: 0.0
                    if (h > 0) {
                        val mHeight = h / 100.0
                        val rawBmi = w / (mHeight * mHeight)
                        bmiResult = Math.round(rawBmi * 10.0) / 10.0
                        bmiCategory = when {
                            rawBmi < 18.5 -> "Underweight"
                            rawBmi < 25.0 -> "Normal Weight"
                            rawBmi < 30.0 -> "Overweight"
                            else -> "Obese"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricLime)
            ) {
                Text("Calculate BMI", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            bmiResult?.let { res ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("BMI Score", color = Color.Gray, fontSize = 10.sp)
                        Text("$res", color = ElectricLime, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Classification", color = Color.Gray, fontSize = 10.sp)
                        Text(bmiCategory, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WebAboutScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("ABOUT EXCELERATE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Since 2018, EXCELERATE Performance Centers have defined high-intensity, black-label fitness across Los Angeles. Our mission is direct: zero-ego athletic development. We offer high-performance bio-mechanics, state-of-the-art weights, and dynamic custom wellness layouts designed for professionals who respect effort.",
            color = Color.LightGray,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("AMENITIES", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        val amenities = listOf(
            "★ Elite Biomechanic Equipment Floors",
            "★ Deep Cryo-Therapy Recovery Suites",
            "★ Multi-Zone HIIT Training Stations",
            "★ Personalized Nutrition Juice & Hydration bars",
            "★ Premium Dynamic Locker Rooms and laundry"
        )
        amenities.forEach { item ->
            Text(item, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun WebClassesScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("OUR CLASS OFFERINGS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Book dynamic training camps or individual slots.", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        ClassRow(title = "Strength Lab", desc = "Hypertrophy focus, deadlifts, and squats guided by sports scientists.")
        ClassRow(title = "HIIT Core Conditioning", desc = "High-octane metabolic intervals using ropes, sprint lanes, and assault bikes.")
        ClassRow(title = "Power Yoga Flow", desc = "Enhance mobility, core stability, and athletic recovery breathing.")
        ClassRow(title = "Elite Private Coaching", desc = "One-on-one tailored program analysis to scale your metric goals.")
    }
}

@Composable
fun ClassRow(title: String, desc: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = ElectricLime, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
fun WebDietDetailsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("DIET & NUTRITION SCHEMES", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Precision fuel makes champions. View our recommended foundational templates below.", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        DietTemplateBox(
            title = "Elite Hypertrophy Template",
            macros = "40% Protein | 30% Carbs | 30% Fats",
            target = "Target: Bulking and rebuilding torn fiber safely.",
            meals = "Post-Workout: Salmon Bowl with quinoa & avocado.\nSnack: Greek yogurt with honey and berries."
        )
        Spacer(modifier = Modifier.height(12.dp))
        DietTemplateBox(
            title = "Lean Agility Burn",
            macros = "50% Protein | 20% Carbs | 30% Fats",
            target = "Target: Shredding fat reserves while preserving muscle integrity.",
            meals = "Post-Workout: Hydrolyzed isolate whey whey + clean chicken breast.\nSnack: Almond butter and celery stems."
        )
    }
}

@Composable
fun DietTemplateBox(title: String, macros: String, target: String, meals: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = ElectricLime, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(macros, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(target, color = Color.LightGray, fontSize = 11.sp, fontStyle = FontStyle.Italic)
            Divider(color = BorderGrey, modifier = Modifier.padding(vertical = 8.dp))
            Text(meals, color = Color.LightGray, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
fun WebTransformationsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("TRANSFORMATION STORIES", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Verified athletic developments from real regular participants.", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        TransformationRow(
            name = "Sarah J.",
            change = "-14kg Weight Loss / +8% Hypertrophy",
            desc = "\"Consistent bio-mechanic guidelines in the Strength Lab cured my back posture and completely rebuilt my confidence!\""
        )
        TransformationRow(
            name = "Alex Rivera",
            change = "-2.4kg Lean Tone in 30 days",
            desc = "\"Simply adhering to the automatic app-guided water targets and the daily Power drills was incredibly straightforward and rewarding.\""
        )
    }
}

@Composable
fun TransformationRow(name: String, change: String, desc: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BorderGrey),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(change, color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = Color.LightGray, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
fun WebContactScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("CONTACT & LOCATIONS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Have queries? Reach our team immediately daily.", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Location Box with Address
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, BorderGrey)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("HEADQUARTERS", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("880 Elite Performance Way,\nLos Angeles, CA 90012", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                Text("Phone: +1 (555) 912-8822\nEmail: contact@exceleratecenters.com", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Map coordinates simulation:", color = Color.Gray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))

        // Visual Map Mockup using the actual linked image!
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQAcL6jASeFMMgCJJZy3YneJFH6xnGEVRXY70rerkLL17JFSHuc2h7bSIDU20pI_OOENRZDLJAXhIgz4rt9QA4O5m8WvZAAmppXqBR8JUhXtamCBac_LQ5QAmfiwxRuudtgRXNkwReciBsfP9Bie2WvxTk13cALiI_LX39RIn3WxmKj1AJicbV4JDaTNj3hMibMVJLWTORRechk6GTwLYhT5mk5avOQT3-X74QIBfyokMuguepchXyILbkxjLtSaK0w-V4MDzO8SU",
            contentDescription = "Cosmo Lime Map Visualization",
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("FREQUENTLY ASKED QUESTIONS (FAQ)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        FaqItem(q = "Can I access the facility 3am?", a = "Yes, all our plans provide full 24/7 key-card barcode check-in privileges.")
        FaqItem(q = "Is the locker laundry free?", a = "Elite annual and Pro-Elite members receive complete complementary towels and laundry services.")
        FaqItem(q = "Can I freeze membership?", a = "Yes, dynamic members can freeze membership up to 3 months via the Admin Dashboard option or writing our desk.")
    }
}

@Composable
fun FaqItem(q: String, a: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text("Q: $q", color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text("A: $a", color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
fun WebFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EXCELERATE PERFORMANCE CENTERS", color = ElectricLime, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("The absolute black-label destination for athletic focus. Since 2018.", color = Color.Gray, fontSize = 10.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text("© 2026 EXCELERATE. ALL RIGHTS RESERVED.", color = Color.DarkGray, fontSize = 9.sp)
    }
}
