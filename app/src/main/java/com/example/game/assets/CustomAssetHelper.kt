package com.example.game.assets

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Helper to dynamically load customizable assets from GitHub-friendly folders:
 * 1. background_image/ (custom background photo/wallpaper)
 * 2. audio/ (winning, wrong_move, and app_start audios)
 * 3. app_picture/ (custom character photo/avatar/icon)
 */
object CustomAssetHelper {

    private var cachedBackground: ImageBitmap? = null
    private var cachedAppPic: ImageBitmap? = null
    private var backgroundChecked = false
    private var appPicChecked = false

    fun getBackgroundImage(context: Context): ImageBitmap? {
        if (backgroundChecked) return cachedBackground
        backgroundChecked = true
        try {
            // Check in assets/background_image/
            val list = context.assets.list("background_image") ?: emptyArray()
            val imageFile = list.firstOrNull { name ->
                isImageFile(name)
            }
            if (imageFile != null) {
                context.assets.open("background_image/$imageFile").use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        cachedBackground = bitmap.asImageBitmap()
                        return cachedBackground
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }

    fun getAppPicture(context: Context): ImageBitmap? {
        if (appPicChecked) return cachedAppPic
        appPicChecked = true
        try {
            // Check in assets/app_picture/
            val list = context.assets.list("app_picture") ?: emptyArray()
            val imageFile = list.firstOrNull { name ->
                isImageFile(name)
            }
            if (imageFile != null) {
                context.assets.open("app_picture/$imageFile").use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        cachedAppPic = bitmap.asImageBitmap()
                        return cachedAppPic
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }

    enum class AudioCategory {
        APP_START,
        WINNING,
        WRONG_MOVE
    }

    fun getAudioFd(context: Context, category: AudioCategory): AssetFileDescriptor? {
        try {
            val list = context.assets.list("audio") ?: emptyArray()
            val targetFile = when (category) {
                AudioCategory.APP_START -> {
                    list.firstOrNull { name ->
                        val lower = name.lowercase()
                        (lower.contains("start") || lower.contains("open") || lower.contains("intro") || lower.contains("welcome")) &&
                                isAudioFile(lower)
                    } ?: list.firstOrNull { it.lowercase().endsWith(".mp3") }
                }
                AudioCategory.WINNING -> {
                    list.firstOrNull { name ->
                        val lower = name.lowercase()
                        (lower.contains("win") || lower.contains("victory") || lower.contains("success") || lower.contains("complete")) &&
                                isAudioFile(lower)
                    }
                }
                AudioCategory.WRONG_MOVE -> {
                    list.firstOrNull { name ->
                        val lower = name.lowercase()
                        (lower.contains("wrong") || lower.contains("error") || lower.contains("blocked") || lower.contains("ghalat") || lower.contains("fail")) &&
                                isAudioFile(lower)
                    }
                }
            }
            if (targetFile != null) {
                return context.assets.openFd("audio/$targetFile")
            }
        } catch (_: Exception) {}
        return null
    }

    private fun isImageFile(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".webp")
    }

    private fun isAudioFile(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".mp3") || lower.endsWith(".wav") ||
                lower.endsWith(".ogg") || lower.endsWith(".m4a") || lower.endsWith(".aac")
    }
}
