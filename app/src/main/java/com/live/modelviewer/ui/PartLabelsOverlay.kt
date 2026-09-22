package com.live.modelviewer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.live.modelviewer.model.ModelContainerState
import kotlin.math.roundToInt

@Composable
fun PartLabelsOverlay(
    activeModels: List<ModelContainerState>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = with(density) { 2.dp.toPx() }
            val anchorRadiusPx = with(density) { 4.dp.toPx() }
            val lineOffsetXPx = with(density) { 30.dp.toPx() }
            val lineOffsetYPx = with(density) { (-15).dp.toPx() }

            for (model in activeModels) {
                if (model.showLabels) {
                    for (label in model.projectedLabels) {
                        if (label.isVisible) {
                            val anchorX = label.screenX
                            val anchorY = label.screenY

                            val labelTargetX = anchorX + lineOffsetXPx
                            val labelTargetY = anchorY + lineOffsetYPx

                            drawCircle(
                                color = Color(0xFF38BDF8),
                                radius = anchorRadiusPx,
                                center = Offset(anchorX, anchorY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = anchorRadiusPx * 0.5f,
                                center = Offset(anchorX, anchorY)
                            )

                            drawLine(
                                color = Color(0xFF38BDF8),
                                start = Offset(anchorX, anchorY),
                                end = Offset(labelTargetX, labelTargetY),
                                strokeWidth = strokeWidthPx,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        for (model in activeModels) {
            if (model.showLabels) {
                for (label in model.projectedLabels) {
                    if (label.isVisible) {
                        val offsetX = (label.screenX + 30).roundToInt()
                        val offsetY = (label.screenY - 25).roundToInt()

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX, offsetY) }
                                .background(
                                    color = Color(0xFF0F172A).copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF38BDF8),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label.labelText,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
