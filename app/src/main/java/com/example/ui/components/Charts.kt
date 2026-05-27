package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ElectricLime = Color(0xFFD9FF00)
val DarkGreyBg = Color(0xFF0F172A)
val SurfaceGrey = Color(0x12FFFFFF)
val BorderGrey = Color(0x1FFFFFFF)
val ErrorRed = Color(0xFFFFB4AB)
val SoftCyan = Color(0xFF38BDF8)

@Composable
fun CustomLineChart(
    points: List<Double>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxVal = (points.maxOrNull() ?: 100.0) + 1.0
    val minVal = (points.minOrNull() ?: 0.0) - 1.0
    val diff = maxVal - minVal

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { index, value ->
            val ratio = (value - minVal) / diff
            val y = height - (ratio * height).toFloat()
            val x = index * spacing

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            if (index == points.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Draw fill gradient
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(ElectricLime.copy(alpha = 0.35f), Color.Transparent)
            )
        )

        // Draw line
        drawPath(
            path = path,
            color = ElectricLime,
            style = Stroke(width = 4.dp.toPx())
        )

        // Draw point dots
        points.forEachIndexed { index, value ->
            val ratio = (value - minVal) / diff
            val y = height - (ratio * height).toFloat()
            val x = index * spacing

            drawCircle(
                color = ElectricLime,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun CustomBarChart(
    heights: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.Bottom
    ) {
        heights.forEachIndexed { index, ratio ->
            val animatedHeightRatio by animateFloatAsState(
                targetValue = ratio,
                animationSpec = tween(durationMillis = 800),
                label = "barHeight"
            )

            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(animatedHeightRatio)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (index == heights.lastIndex) {
                                    listOf(ElectricLime, ElectricLime.copy(alpha = 0.7f))
                                } else {
                                    listOf(BorderGrey, BorderGrey.copy(alpha = 0.3f))
                                }
                            ),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = labels.getOrElse(index) { "" },
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
