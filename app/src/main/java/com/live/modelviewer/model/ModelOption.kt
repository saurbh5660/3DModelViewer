package com.live.modelviewer.model

data class ModelOption(val name: String, val assetPath: String)

val AVAILABLE_MODELS = listOf(
    ModelOption("Bulb", "models/Bulb.glb"),
    ModelOption("Fiagena", "models/Fiagena.glb"),
    ModelOption("Lungs", "models/Lungs.glb"),
    ModelOption("Microscope", "models/Microscope.glb"),
    ModelOption("Solar System", "models/solarsystem.glb")
)
