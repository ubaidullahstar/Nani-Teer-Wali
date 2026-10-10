package com.example.game.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.game.assets.CustomAssetHelper

/**
 * Renders the custom background photo from `background_image/` folder
 * (or fallback to Nani photo) with an atmospheric gradient overlay
 * to maintain pristine contrast, sharpness, and gameplay readability.
 */
@Composable
fun GameBackground(
    dimAmount: Float = 0.55f,
    overlayColor: Color = Color(0xFF0B132B),
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val customBitmap = remember { CustomAssetHelper.getBackgroundImage(context) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (customBitmap != null) {
            Image(
                bitmap = customBitmap,
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.game_bg_nani),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Dark gradient scrim/overlay to ensure crisp puzzle visibility and contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            overlayColor.copy(alpha = dimAmount + 0.15f),
                            overlayColor.copy(alpha = dimAmount),
                            overlayColor.copy(alpha = dimAmount + 0.25f)
                        )
                    )
                )
        )

        // Game UI content
        content()
    }
}
