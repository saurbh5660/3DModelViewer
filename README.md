# 3D Model Viewer — Android Screening Task

A single-activity Android app written in Kotlin using Google Filament to render and manipulate multiple 3D GLB models concurrently on screen.

## Project Overview

The app allows users to dynamically load 3D models onto an interactive canvas. Each model resides inside its own floating container card with two distinct gesture modes:

* **Normal Mode (Container Controls)**:
  * 1-finger drag moves the container across the screen.
  * 2-finger pinch resizes the container bounds.
* **Interaction Mode (3D Model Controls)**:
  * 1-finger drag rotates the 3D model inside the locked container.
  * 2-finger pinch zooms the 3D content scale.
* **Control Buttons**: Each container has persistent buttons to toggle between modes, toggle 2D part labels, and close/destroy the model instance.
* **2D Part Labels**: Reads `extras.prop` strings from the GLB header and projects 3D node world coordinates to 2D screen space every frame to draw anchored text labels and connector lines.

## 3D Library Choice: Google Filament

I chose **Google Filament** (`com.google.android.filament`) over high-level wrappers like SceneView for several technical reasons:

1. **Low Memory Overhead & Performance**: Filament is Google's native C++ physically-based renderer built for mobile GPUs. It provides predictable frame rates (30+ FPS) on entry-level Android devices (2–3 GB RAM).
2. **Explicit Resource Disposal**: Filament allows direct destruction of entities and scene nodes on disposal, preventing memory leaks when models are closed.
3. **GLTF Extras Access**: `gltfio` allows direct node traversal and transformation matrix extraction required for projecting 2D part labels.

## Technical Implementation & Optimizations

* **ViewModel State Retention**: `MainViewModel` retains active model container states (`activeModels`) across screen orientation changes.
* **Transparent TextureView Layering**: Used `TextureView` with `isOpaque = false` for each model container so Jetpack Compose can clip, layer, and handle touch gestures over multiple floating cards without hardware surface z-index flickering.
* **Zero-Copy Asset Loading**: Added `noCompress += "glb"` in `app/build.gradle.kts` so GLB files are memory-mapped directly from storage without RAM duplication.
* **GLB Header Parser**: `GltfLabelParser` reads binary Chunk 0 JSON headers directly to parse `extras.prop` labels in milliseconds without needing to fully instantiate renderables beforehand.
* **Safe Disposal Lifecycle**: Removed scene entities on `onDispose` (`scene.removeEntities`) to prevent native C++ JNI crashes (`SIGSEGV`) when closing models or rotating the screen.

## Trade-offs & Limitations

* **TextureView vs SurfaceView**: While `SurfaceView` has slightly lower latency on some devices, `TextureView` was required to support multiple overlapping Compose cards, rounded corners, and transparency without z-index artifacts.
* **Lighting & Environment**: Used basic ambient lighting without heavy IBL (Image-Based Lighting) skybox textures to keep the APK size compact and save VRAM on low-end GPUs.

## Testing & Environment

* **Target SDK**: 37 (Min SDK 24)
* **Tested On**: Android Emulator / Pixel profile & low-RAM device profile (SDK 34, 2–3 GB RAM).
* **Build Artifact**: `app/build/outputs/apk/debug/app-debug.apk`
