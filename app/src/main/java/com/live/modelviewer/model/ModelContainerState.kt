package com.live.modelviewer.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class InteractionMode {
    NORMAL,       // 1-finger moves container, 2-finger resizes container
    INTERACTION   // 1-finger rotates 3D model, 2-finger zooms 3D model
}

data class ProjectedLabel(
    val labelText: String,
    val screenX: Float,
    val screenY: Float,
    val isVisible: Boolean = true
)

class ModelContainerState(
    val id: String,
    val modelName: String,
    val assetPath: String,
    initialX: Float = 100f,
    initialY: Float = 200f,
    initialWidth: Float = 600f,
    initialHeight: Float = 600f,
    val parsedLabels: List<PartLabelInfo> = emptyList()
) {
    // Container Screen Bounds State
    var offsetX by mutableFloatStateOf(initialX)
    var offsetY by mutableFloatStateOf(initialY)
    var width by mutableFloatStateOf(initialWidth)
    var height by mutableFloatStateOf(initialHeight)

    // Mode States
    var mode by mutableStateOf(InteractionMode.NORMAL)
    var showLabels by mutableStateOf(false)

    // 3D Model Transformation State inside Container
    var rotationX by mutableFloatStateOf(0f)
    var rotationY by mutableFloatStateOf(0f)
    var scale by mutableFloatStateOf(1.0f)

    // Projected 2D Part Labels (updated per render frame)
    var projectedLabels by mutableStateOf<List<ProjectedLabel>>(emptyList())

    fun toggleMode() {
        mode = if (mode == InteractionMode.NORMAL) InteractionMode.INTERACTION else InteractionMode.NORMAL
    }

    fun toggleLabels() {
        showLabels = !showLabels
    }
}
