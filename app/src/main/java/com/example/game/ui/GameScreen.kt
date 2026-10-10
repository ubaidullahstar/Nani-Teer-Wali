package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.ui.components.AppPictureImage
import com.example.game.ui.components.GameBackground
import com.example.game.logic.ArrowGameEngine
import com.example.game.logic.LevelRegistry
import com.example.game.model.ArrowSnake
import com.example.game.model.ArrowState
import com.example.game.model.EscapeCheckResult
import com.example.game.model.GameLevel
import com.example.game.model.ThemeMode
import com.example.game.sound.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    currentLevelNum: Int,
    soundManager: SoundManager,
    onNavigateToLevelSelect: () -> Unit,
    onLevelAdvanced: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var levelData by remember(currentLevelNum) {
        mutableStateOf(LevelRegistry.getLevel(currentLevelNum))
    }

    // Active snakes state
    val snakes = remember(currentLevelNum) {
        mutableStateListOf<ArrowSnake>().apply {
            addAll(levelData.snakes)
        }
    }

    // Hearts (lives)
    var hearts by remember(currentLevelNum) { mutableIntStateOf(5) }
    var movesCount by remember(currentLevelNum) { mutableIntStateOf(0) }
    var hintsRemaining by remember { mutableIntStateOf(3) }
    val undoStack = remember(currentLevelNum) { mutableStateListOf<Int>() }

    var isLevelWon by remember(currentLevelNum) { mutableStateOf(false) }
    var isGameOver by remember(currentLevelNum) { mutableStateOf(false) }

    var showHowToPlay by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var theme by remember { mutableStateOf(ThemeMode.CLASSIC) }

    // Reload level
    fun resetCurrentLevel() {
        levelData = LevelRegistry.getLevel(currentLevelNum)
        snakes.clear()
        snakes.addAll(levelData.snakes)
        hearts = 5
        movesCount = 0
        undoStack.clear()
        isLevelWon = false
        isGameOver = false
    }

    // Handle user tap on arrow
    fun handleSnakeTap(tappedSnake: ArrowSnake) {
        if (isLevelWon || isGameOver) return
        if (tappedSnake.state != ArrowState.IDLE) return

        movesCount++

        val result = ArrowGameEngine.checkEscape(
            snake = tappedSnake,
            allSnakes = snakes,
            gridWidth = levelData.gridWidth,
            gridHeight = levelData.gridHeight
        )

        when (result) {
            is EscapeCheckResult.Clear -> {
                soundManager.playEscape()
                undoStack.add(tappedSnake.id)

                // Animate escape slither
                val index = snakes.indexOfFirst { it.id == tappedSnake.id }
                if (index != -1) {
                    snakes[index] = tappedSnake.copy(
                        state = ArrowState.ESCAPING,
                        isHinted = false
                    )

                    scope.launch {
                        val anim = Animatable(0f)
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 320, easing = LinearEasing)
                        ) {
                            if (index < snakes.size) {
                                snakes[index] = snakes[index].copy(escapeProgress = this.value)
                            }
                        }
                        if (index < snakes.size) {
                            snakes[index] = snakes[index].copy(
                                state = ArrowState.ESCAPED,
                                escapeProgress = 1f
                            )
                        }

                        // Check win condition
                        val remaining = snakes.count { it.state != ArrowState.ESCAPED }
                        if (remaining == 0) {
                            delay(100)
                            soundManager.playLevelComplete()
                            isLevelWon = true
                        }
                    }
                }
            }

            is EscapeCheckResult.Blocked -> {
                soundManager.playBlocked()
                val index = snakes.indexOfFirst { it.id == tappedSnake.id }
                if (index != -1) {
                    snakes[index] = tappedSnake.copy(
                        state = ArrowState.COLLIDING,
                        collisionTarget = result.collisionPoint,
                        isHinted = false
                    )

                    scope.launch {
                        val anim = Animatable(0f)
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
                        ) {
                            if (index < snakes.size) {
                                snakes[index] = snakes[index].copy(collisionProgress = this.value)
                            }
                        }
                        if (index < snakes.size) {
                            snakes[index] = snakes[index].copy(
                                state = ArrowState.IDLE,
                                collisionProgress = 0f,
                                collisionTarget = null
                            )
                        }

                        // Lose life
                        hearts = maxOf(0, hearts - 1)
                        soundManager.playHeartLost()
                        if (hearts == 0) {
                            delay(150)
                            isGameOver = true
                        }
                    }
                }
            }
        }
    }

    // Hint trigger
    fun requestHint() {
        if (hintsRemaining <= 0 || isLevelWon || isGameOver) return
        val clearSnake = ArrowGameEngine.findClearSnake(snakes, levelData.gridWidth, levelData.gridHeight)
        if (clearSnake != null) {
            val idx = snakes.indexOfFirst { it.id == clearSnake.id }
            if (idx != -1) {
                // Clear any existing hint
                for (i in snakes.indices) {
                    if (snakes[i].isHinted) snakes[i] = snakes[i].copy(isHinted = false)
                }
                snakes[idx] = snakes[idx].copy(isHinted = true)
                hintsRemaining--
                soundManager.playHint()
            }
        }
    }

    // Undo trigger
    fun triggerUndo() {
        if (undoStack.isEmpty() || isLevelWon || isGameOver) return
        val lastId = undoStack.removeLast()
        val idx = snakes.indexOfFirst { it.id == lastId }
        if (idx != -1) {
            snakes[idx] = snakes[idx].copy(
                state = ArrowState.IDLE,
                escapeProgress = 0f,
                collisionProgress = 0f
            )
            soundManager.playTap()
        }
    }

    GameBackground(
        dimAmount = 0.65f,
        overlayColor = Color(0xFF0F172A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Bar: Back button on left, Nani Logo + Level Title in center, Settings on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to Mode Select / Levels button
                IconButton(
                    onClick = onNavigateToLevelSelect,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFDDE3F8), CircleShape)
                        .testTag("back_to_levels_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Home",
                        tint = Color(0xFF4A60D0)
                    )
                }

                // Centered Nani Logo + Level Number + Hearts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF4A60D0), CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        AppPictureImage(
                            contentDescription = "App Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Maze Level $currentLevelNum of 10",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A60D0)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Hearts Row: 5 Red Hearts
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                val isAlive = i <= hearts
                                Icon(
                                    imageVector = if (isAlive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isAlive) "Heart Active" else "Heart Lost",
                                    tint = if (isAlive) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .size(18.dp)
                                        .testTag("heart_$i")
                                )
                            }
                        }
                    }
                }

                // Quick buttons on the right
                Row {
                    IconButton(
                        onClick = { showHowToPlay = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("help_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Rules",
                            tint = Color(0xFF64748B)
                        )
                    }
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Power-up action row: Hint, Undo, Restart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hint Button
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                    modifier = Modifier
                        .clickable(enabled = hintsRemaining > 0) { requestHint() }
                        .testTag("hint_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hint ($hintsRemaining)",
                            color = Color(0xFF4F46E5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Undo Button
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .clickable(enabled = undoStack.isNotEmpty()) { triggerUndo() }
                        .testTag("undo_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (undoStack.isNotEmpty()) Color(0xFF334155) else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Undo",
                            color = if (undoStack.isNotEmpty()) Color(0xFF334155) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }

                // Restart Button
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .clickable { resetCurrentLevel() }
                        .testTag("restart_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reset",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Main Puzzle Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                ArrowCanvas(
                    snakes = snakes,
                    gridWidth = levelData.gridWidth,
                    gridHeight = levelData.gridHeight,
                    theme = theme,
                    onSnakeTapped = { handleSnakeTap(it) }
                )
            }

            // Bottom bar: Remaining arrows counter & quick stats
            val remainingCount = snakes.count { it.state != ArrowState.ESCAPED }
            val totalCount = snakes.size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remaining: $remainingCount / $totalCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = "Moves: $movesCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "This game is made by Ubaid",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 6.dp)
            )
        }
    }

    // Win Modal
    if (isLevelWon) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("win_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFF59E0B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppPictureImage(
                            contentDescription = "App Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (currentLevelNum >= 10) "Maze Master Complete! 🏆" else "Maze Level $currentLevelNum Cleared! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (currentLevelNum >= 10) "Aapne tamam 10 Maze Levels kamyabi se escape kar liye!" else "All maze arrows successfully escaped!",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stars rating
                    val starsEarned = when {
                        hearts >= 4 -> 3
                        hearts >= 2 -> 2
                        else -> 1
                    }
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < starsEarned) Color(0xFFF59E0B) else Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isLevelWon = false
                            val next = if (currentLevelNum >= 10) 1 else currentLevelNum + 1
                            onLevelAdvanced(next)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("next_level_button")
                    ) {
                        Text(
                            text = if (currentLevelNum >= 10) "Play Again from Level 1" else "Next Level",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { resetCurrentLevel() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("replay_button")
                    ) {
                        Text(
                            text = "Replay Level",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4F46E5)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This game is made by Ubaid",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }

    // Game Over Modal
    if (isGameOver) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("game_over_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFFEE2E2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Out of Lives!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Too many collisions. Trace the exit rays carefully before tapping!",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            hearts = 5
                            isGameOver = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("refill_lives_button")
                    ) {
                        Text(
                            text = "Refill 5 Hearts ❤️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { resetCurrentLevel() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("retry_level_button")
                    ) {
                        Text(
                            text = "Restart Level",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }

    // Settings Modal
    if (showSettings) {
        SettingsDialog(
            isSoundEnabled = soundManager.isSoundEnabled,
            onSoundToggled = { soundManager.isSoundEnabled = it },
            isHapticsEnabled = soundManager.isHapticsEnabled,
            onHapticsToggled = { soundManager.isHapticsEnabled = it },
            currentTheme = theme,
            onThemeSelected = { theme = it },
            onDismiss = { showSettings = false }
        )
    }

    // How to Play Modal
    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }
}
