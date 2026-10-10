package com.example.game.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.game.assets.CustomAssetHelper

/**
 * Renders the game's character / profile picture.
 * Dynamically loads from the `app_picture/` folder if customized,
 * or falls back to the default Nani avatar.
 */
@Composable
fun AppPictureImage(
    modifier: Modifier = Modifier,
    contentDescription: String? = "App Character Picture",
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val customBitmap = remember { CustomAssetHelper.getAppPicture(context) }

    if (customBitmap != null) {
        Image(
            bitmap = customBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.nani_avatar),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
