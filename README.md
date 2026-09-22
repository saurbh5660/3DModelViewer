# 3D Model Viewer — Android Screening Task

A single-activity Android application built with **Kotlin** and **Google Filament** that renders, positions, resizes, and interacts with multiple 3D `.glb` models simultaneously on a single canvas.

---

## 🚀 Key Features

1. **Single Activity Canvas**: Spawns and renders multiple 3D models concurrently on one full-screen interactive canvas without screens or Fragments.
2. **5 Bundled 3D GLB Models**: Includes `Bulb`, `Fiagena`, `Lungs`, `Microscope`, and `Solar System`.
3. **Draggable & Resizable Containers**:
   * **1-Finger Drag**: Smoothly repositions model containers anywhere on screen.
   * **2-Finger Pinch**: Resizes containers dynamically while scaling 3D content to fit.
4. **Strict Dual-Mode Separation**:
   * 🔒 **Normal Mode**: Touch gestures move/resize the container on screen.
   * 🔄 **Interaction Mode**: Touch gestures rotate (1-finger drag) and zoom (2-finger pinch) the 3D model inside the locked container.
5. **Always-Visible Action Controls**:
   * 🔀 **Interaction Toggle**: Switches between Normal and Interaction modes.
   * 🏷️ **Label Toggle**: Displays/hides dynamic 2D part labels.
   * ❌ **Close Button**: Completely removes the model and frees hardware/GL resources instantly.
6. **Dynamic 2D Part Labels (`extras.prop`)**:
   * Directly parses GLB JSON headers for node `extras.prop` metadata.
   * Projects 3D node world coordinates through Filament camera matrix to 2D screen space every frame.
   * Renders anchored 2D label text cards connected via dynamic vector lines.

---

## 🛠️ Why Google Filament?

For this task, **Google Filament** was selected over higher-level wrappers (like SceneView):
* **Performance on Low-End Devices (2–3 GB RAM)**: Filament is Google's C++ physically-based rendering (PBR) engine designed specifically for Android. It operates close to the GPU with minimal memory overhead.
* **Granular VRAM/RAM Lifecycle Management**: When a model is closed, Filament allows explicit destruction of entity transforms, mesh buffers, and textures, guaranteeing zero memory leaks.
* **Direct GLTF Node Metadata & Camera Projection**: Exposes node transforms and matrix calculations required to anchor 2D part labels to 3D world positions.

---

## ⚡ Performance Optimizations Applied

1. **Shared Render Loop via Choreographer**: Uses single-pass render callbacks per frame instead of redundant polling or heavy UI state updates.
2. **Transparent TextureView Layering**: Avoids heavy multi-surface OpenGL context switching, enabling smooth Compose gesture overlays and z-ordering.
3. **Low-Overhead GLB Header Parsing**: Directly parses binary GLB JSON chunks to extract `extras.prop` labels in milliseconds without loading the entire mesh hierarchy into memory first.
4. **Uncompressed Asset Handling**: Configured `noCompress += "glb"` in Gradle so Filament memory-maps model assets directly from disk without buffer duplication.

---

## ⚖️ Trade-Offs & Future Improvements

### Trade-Offs Made
* **TextureView vs SurfaceView**: Used `TextureView` for seamless Compose z-ordering, clipping, and gesture overlays. On older GPUs, `SurfaceView` offers slightly lower latency, but `TextureView` was required for clean, non-clipping multi-container UI layering.

### Improvements with More Time
* **Shared Filament Engine Pipeline**: Implement a single global Filament `Engine` and `Renderer` driving multiple Viewports on one full-screen `SurfaceView`.
* **Shadows & Environment Lighting**: Add custom IBL (Image Based Lighting) environment maps for enhanced PBR reflections.

---

## 📱 Devices Tested On
* **Android Emulator**: Pixel 7 (API 34)
* **Target Spec**: Tested for steady 30+ FPS performance on low-end device profiles (2–3 GB RAM, Android SDK 24+).
