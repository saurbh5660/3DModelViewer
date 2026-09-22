package com.live.modelviewer.engine

import android.content.Context
import com.live.modelviewer.model.PartLabelInfo
import org.json.JSONObject
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object GltfLabelParser {

    /**
     * Parses the JSON chunk of a GLB file to extract nodes with "extras": { "prop": "..." }
     */
    fun parseLabelsFromAsset(context: Context, assetPath: String): List<PartLabelInfo> {
        return try {
            context.assets.open(assetPath).use { inputStream ->
                parseLabelsFromStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun parseLabelsFromStream(inputStream: InputStream): List<PartLabelInfo> {
        val headerBytes = ByteArray(12)
        if (inputStream.read(headerBytes) < 12) return emptyList()

        val headerBuffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN)
        val magic = headerBuffer.int
        if (magic != 0x46546C67) return emptyList()

        val version = headerBuffer.int
        val totalLength = headerBuffer.int

        val chunkHeaderBytes = ByteArray(8)
        if (inputStream.read(chunkHeaderBytes) < 8) return emptyList()

        val chunkHeaderBuffer = ByteBuffer.wrap(chunkHeaderBytes).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeaderBuffer.int
        val chunkType = chunkHeaderBuffer.int

        if (chunkType != 0x4E4F534A) return emptyList() // "JSON" chunk type

        val jsonBytes = ByteArray(chunkLength)
        var bytesRead = 0
        while (bytesRead < chunkLength) {
            val read = inputStream.read(jsonBytes, bytesRead, chunkLength - bytesRead)
            if (read == -1) break
            bytesRead += read
        }

        val jsonString = String(jsonBytes, Charsets.UTF_8)
        val rootJson = JSONObject(jsonString)

        val labels = mutableListOf<PartLabelInfo>()
        if (!rootJson.has("nodes")) return labels

        val nodesArray = rootJson.getJSONArray("nodes")
        for (i in 0 until nodesArray.length()) {
            val nodeObj = nodesArray.getJSONObject(i)
            val name = nodeObj.optString("name", "Node_$i")

            var labelText: String? = null
            if (nodeObj.has("extras")) {
                val extrasObj = nodeObj.optJSONObject("extras")
                if (extrasObj != null && extrasObj.has("prop")) {
                    labelText = extrasObj.getString("prop")
                }
            }

            if (labelText == null && nodeObj.has("prop")) {
                labelText = nodeObj.optString("prop")
            }

            if (!labelText.isNullOrEmpty()) {
                val translation = floatArrayOf(0f, 0f, 0f)
                if (nodeObj.has("translation")) {
                    val transArray = nodeObj.getJSONArray("translation")
                    if (transArray.length() >= 3) {
                        translation[0] = transArray.getDouble(0).toFloat()
                        translation[1] = transArray.getDouble(1).toFloat()
                        translation[2] = transArray.getDouble(2).toFloat()
                    }
                }
                labels.add(
                    PartLabelInfo(
                        nodeName = name,
                        labelText = labelText,
                        nodeIndex = i,
                        initialTranslation = translation
                    )
                )
            }
        }
        return labels
    }
}
