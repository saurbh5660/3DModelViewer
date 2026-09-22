package com.live.modelviewer.model

data class PartLabelInfo(
    val nodeName: String,
    val labelText: String,
    val nodeIndex: Int,
    val initialTranslation: FloatArray = floatArrayOf(0f, 0f, 0f)
)
