package com.live.modelviewer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.live.modelviewer.model.InteractionMode
import com.live.modelviewer.model.ModelContainerState
import kotlin.math.roundToInt

@Composable
fun ModelContainerCard(
    state: ModelContainerState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset { IntOffset(state.offsetX.roundToInt(), state.offsetY.roundToInt()) }
            .width(state.width.dp)
            .height(state.height.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .background(
                color = if (state.mode == InteractionMode.INTERACTION) {
                    Color(0xFF1E293B).copy(alpha = 0.85f)
                } else {
                    Color(0xFF0F172A).copy(alpha = 0.75f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (state.mode == InteractionMode.INTERACTION) 2.dp else 1.dp,
                color = if (state.mode == InteractionMode.INTERACTION) {
                    Color(0xFF38BDF8)
                } else {
                    Color(0xFF475569)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(state.mode) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (state.mode == InteractionMode.NORMAL) {
                        state.offsetX = (state.offsetX + pan.x).coerceAtLeast(0f)
                        state.offsetY = (state.offsetY + pan.y).coerceAtLeast(0f)

                        if (zoom != 1.0f) {
                            state.width = (state.width * zoom).coerceIn(180f, 1200f)
                            state.height = (state.height * zoom).coerceIn(180f, 1200f)
                        }
                    } else {
                        state.rotationY += pan.x * 0.4f
                        state.rotationX += pan.y * 0.4f

                        if (zoom != 1.0f) {
                            state.scale = (state.scale * zoom).coerceIn(0.15f, 6.0f)
                        }
                    }
                }
            }
    ) {
        FilamentModelView(
            state = state,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = state.modelName,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { state.toggleMode() },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.mode == InteractionMode.INTERACTION) {
                            Color(0xFF0284C7)
                        } else {
                            Color(0xFF334155)
                        }
                    )
                ) {
                    Icon(
                        imageVector = if (state.mode == InteractionMode.INTERACTION) Icons.Default.Refresh else Icons.Default.Lock,
                        contentDescription = "Toggle Mode",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { state.toggleLabels() },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.showLabels) Color(0xFF16A34A) else Color(0xFF334155)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Toggle Labels",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Model",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Surface(
            color = if (state.mode == InteractionMode.INTERACTION) Color(0xFF0284C7).copy(alpha = 0.9f) else Color(0xFF475569).copy(alpha = 0.7f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = if (state.mode == InteractionMode.INTERACTION) "3D INTERACT MODE" else "DRAG / RESIZE MODE",
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
