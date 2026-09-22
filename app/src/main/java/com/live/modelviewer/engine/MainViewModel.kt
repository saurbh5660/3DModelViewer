package com.live.modelviewer.engine

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.live.modelviewer.model.ModelContainerState
import com.live.modelviewer.model.ModelOption
import java.util.UUID

class MainViewModel : ViewModel() {

    private val _activeModels = mutableStateListOf<ModelContainerState>()
    val activeModels: List<ModelContainerState> get() = _activeModels

    fun addModel(context: Context, modelOption: ModelOption) {
        val initialX = 50f + (_activeModels.size * 60f)
        val initialY = 150f + (_activeModels.size * 60f)

        // Parse part labels from GLB JSON header
        val labels = GltfLabelParser.parseLabelsFromAsset(
            context = context,
            assetPath = modelOption.assetPath
        )

        _activeModels.add(
            ModelContainerState(
                id = UUID.randomUUID().toString(),
                modelName = modelOption.name,
                assetPath = modelOption.assetPath,
                initialX = initialX,
                initialY = initialY,
                initialWidth = 550f,
                initialHeight = 550f,
                parsedLabels = labels
            )
        )
    }

    fun removeModel(modelState: ModelContainerState) {
        _activeModels.remove(modelState)
    }

    override fun onCleared() {
        super.onCleared()
        _activeModels.clear()
    }
}
