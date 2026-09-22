package com.live.modelviewer.ui

import android.graphics.PixelFormat
import android.opengl.Matrix
import android.view.Choreographer
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.utils.ModelViewer
import com.live.modelviewer.model.ModelContainerState
import com.live.modelviewer.model.ProjectedLabel
import java.nio.ByteBuffer

@Composable
fun FilamentModelView(
    state: ModelContainerState,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val frameCallback = remember {
        object : Choreographer.FrameCallback {
            var modelViewer: ModelViewer? = null

            override fun doFrame(frameTimeNanos: Long) {
                modelViewer?.let { viewer ->
                    updateModelTransform(viewer, state)

                    if (state.showLabels && state.parsedLabels.isNotEmpty()) {
                        updateProjectedLabels(viewer, state)
                    }

                    viewer.render(frameTimeNanos)
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    val textureView = remember {
        TextureView(context).apply {
            isOpaque = false
        }
    }

    DisposableEffect(state.id) {
        val modelViewer = ModelViewer(textureView).apply {
            scene.skybox = null
            view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT
            renderer.clearOptions = renderer.clearOptions.apply {
                clear = true
            }
        }

        frameCallback.modelViewer = modelViewer

        try {
            val bytes = context.assets.open(state.assetPath).use { it.readBytes() }
            val buffer = ByteBuffer.allocateDirect(bytes.size).apply {
                put(bytes)
                rewind()
            }
            modelViewer.loadModelGlb(buffer)
            modelViewer.transformToUnitCube()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Choreographer.getInstance().postFrameCallback(frameCallback)

        onDispose {
            Choreographer.getInstance().removeFrameCallback(frameCallback)
            frameCallback.modelViewer = null
            try {
                modelViewer.asset?.let { asset ->
                    modelViewer.scene.removeEntities(asset.entities)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    AndroidView(
        factory = { textureView },
        modifier = modifier
    )
}

private fun updateModelTransform(viewer: ModelViewer, state: ModelContainerState) {
    val asset = viewer.asset ?: return
    val tm = viewer.engine.transformManager
    val rootEntity = asset.root
    val instance = tm.getInstance(rootEntity)

    if (instance != 0) {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)
        Matrix.scaleM(matrix, 0, state.scale, state.scale, state.scale)
        Matrix.rotateM(matrix, 0, state.rotationX, 1f, 0f, 0f)
        Matrix.rotateM(matrix, 0, state.rotationY, 0f, 1f, 0f)

        tm.setTransform(instance, matrix)
    }
}

private fun updateProjectedLabels(viewer: ModelViewer, state: ModelContainerState) {
    val asset = viewer.asset ?: return
    val tm = viewer.engine.transformManager

    val doubleView = DoubleArray(16)
    val doubleProj = DoubleArray(16)
    val viewMatrix = FloatArray(16)
    val projMatrix = FloatArray(16)
    val mvpMatrix = FloatArray(16)

    viewer.camera.getViewMatrix(doubleView)
    viewer.camera.getProjectionMatrix(doubleProj)

    for (i in 0..15) {
        viewMatrix[i] = doubleView[i].toFloat()
        projMatrix[i] = doubleProj[i].toFloat()
    }
    Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

    val projectedList = mutableListOf<ProjectedLabel>()

    for (labelInfo in state.parsedLabels) {
        val entity = asset.getFirstEntityByName(labelInfo.nodeName)
        val worldPos = FloatArray(4)

        if (entity != 0) {
            val instance = tm.getInstance(entity)
            if (instance != 0) {
                val worldTransform = DoubleArray(16)
                tm.getWorldTransform(instance, worldTransform)
                worldPos[0] = worldTransform[12].toFloat()
                worldPos[1] = worldTransform[13].toFloat()
                worldPos[2] = worldTransform[14].toFloat()
                worldPos[3] = 1.0f
            } else {
                worldPos[0] = labelInfo.initialTranslation[0]
                worldPos[1] = labelInfo.initialTranslation[1]
                worldPos[2] = labelInfo.initialTranslation[2]
                worldPos[3] = 1.0f
            }
        } else {
            worldPos[0] = labelInfo.initialTranslation[0]
            worldPos[1] = labelInfo.initialTranslation[1]
            worldPos[2] = labelInfo.initialTranslation[2]
            worldPos[3] = 1.0f
        }

        val clipPos = FloatArray(4)
        Matrix.multiplyMV(clipPos, 0, mvpMatrix, 0, worldPos, 0)

        val w = clipPos[3]
        if (w > 0.001f) {
            val ndcX = clipPos[0] / w
            val ndcY = clipPos[1] / w

            val localX = (ndcX + 1.0f) * 0.5f * state.width
            val localY = (1.0f - ndcY) * 0.5f * state.height

            val screenX = state.offsetX + localX
            val screenY = state.offsetY + localY

            projectedList.add(
                ProjectedLabel(
                    labelText = labelInfo.labelText,
                    screenX = screenX,
                    screenY = screenY,
                    isVisible = true
                )
            )
        }
    }

    state.projectedLabels = projectedList
}
