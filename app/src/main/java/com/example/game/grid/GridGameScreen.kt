package com.example.game.grid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.game.ui.components.GameBackground
import com.example.game.model.ArrowState
import com.example.game.model.Direction
import com.example.game.sound.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GridGameScreen(
    currentLevelNum: Int,
    soundManager: SoundManager,
    onNavigateToMazeMode: () -> Unit,
    onLevelAdvanced: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var levelData by remember(currentLevelNum) {
        mutableStateOf(GridLevelRegistry.getLevel(currentLevelNum))
    }

    val arrows = remember(currentLevelNum) {
        mutableStateListOf<GridArrow>().apply {
            addAll(levelData.arrows)
        }
    }

    var movesCount by remember(currentLevelNum) { mutableIntStateOf(0) }
    var isLevelWon by remember(currentLevelNum) { mutableStateOf(false) }

    fun resetLevel() {
        levelData = GridLevelRegistry.getLevel(currentLevelNum)
        arrows.clear()
        arrows.addAll(levelData.arrows)
        movesCount = 0
        isLevelWon = false
    }

    fun handleArrowTap(arrow: GridArrow) {
        if (isLevelWon) return
        if (arrow.state != ArrowState.IDLE) return

        movesCount++
        soundManager.playTap()

        val escapeResult = GridGameEngine.checkEscape(arrow, arrows, levelData.gridSize)

        when (escapeResult) {
            is GridEscapeResult.Clear -> {
                soundManager.playEscape()
                val index = arrows.indexOfFirst { it.id == arrow.id }
                if (index != -1) {
                    arrows[index] = arrow.copy(state = ArrowState.ESCAPING, isHinted = false)

                    scope.launch {
                        val anim = Animatable(0f)
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 280, easing = LinearEasing)
                        ) {
                            if (index < arrows.size) {
                                arrows[index] = arrows[index].copy(slideProgress = this.value)
                            }
                        }
                        if (index < arrows.size) {
                            arrows[index] = arrows[index].copy(
                                state = ArrowState.ESCAPED,
                                slideProgress = 1f
                            )
                        }

                        // Check Win Condition: all arrows escaped!
                        val remaining = arrows.count { it.state != ArrowState.ESCAPED }
                        if (remaining == 0) {
                            delay(120)
                            soundManager.playLevelComplete()
                            isLevelWon = true
                        }
                    }
                }
            }

            is GridEscapeResult.Blocked -> {
                soundManager.playBlocked()
                val index = arrows.indexOfFirst { it.id == arrow.id }
                if (index != -1) {
                    arrows[index] = arrow.copy(state = ArrowState.COLLIDING, isHinted = false)

                    scope.launch {
                        val anim = Animatable(0f)
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        ) {
                            if (index < arrows.size) {
                                arrows[index] = arrows[index].copy(bumpProgress = this.value)
                            }
                        }
                        if (index < arrows.size) {
                            arrows[index] = arrows[index].copy(
                                state = ArrowState.IDLE,
                                bumpProgress = 0f
                            )
                        }
                    }
                }
            }
        }
    }

    fun requestHint() {
        if (isLevelWon) return
        val clear = GridGameEngine.findClearArrow(arrows, levelData.gridSize)
        if (clear != null) {
            val idx = arrows.indexOfFirst { it.id == clear.id }
            if (idx != -1) {
                for (i in arrows.indices) {
                    if (arrows[i].isHinted) arrows[i] = arrows[i].copy(isHinted = false)
                }
                arrows[idx] = arrows[idx].copy(isHinted = true)
                soundManager.playHint()
            }
        }
    }

    // Modern Dark-Mode Sleek Styling with Nani in Background
    GameBackground(
        dimAmount = 0.65f,
        overlayColor = Color(0xFF0B132B)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Nani's Face Logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Nani Face Logo in a circular avatar frame
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF38BDF8), CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nani_avatar),
                            contentDescription = "Nani Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Nani Teer wali",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF8FAFC),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Level $currentLevelNum of 10",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                // Switch to Viral Maze Mode Button
                OutlinedButton(
                    onClick = onNavigateToMazeMode,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                    modifier = Modifier.testTag("switch_to_maze_button")
                ) {
                    Text(text = "Maze Mode", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // HUD Action Row (Level info, Moves, Reset, Hint)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Moves Counter
                    Column {
                        Text(
                            text = "MOVES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$movesCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    // Remaining Arrows Counter
                    val remaining = arrows.count { it.state != ArrowState.ESCAPED }
                    Column {
                        Text(
                            text = "REMAINING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$remaining / ${arrows.size}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    // Hint Button
                    IconButton(
                        onClick = { requestHint() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF334155), CircleShape)
                            .testTag("grid_hint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset Level Button
                    IconButton(
                        onClick = { resetLevel() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF334155), CircleShape)
                            .testTag("grid_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Level",
                            tint = Color(0xFFF8FAFC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Puzzle Grid Area (3x3 up to 6x6)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val gridSize = levelData.gridSize

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF162032))
                        .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                        .padding(14.dp)
                ) {
                    val cellSize = maxWidth / gridSize

                    // Render Grid Cells
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (r in 0 until gridSize) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (c in 0 until gridSize) {
                                    val arrow = arrows.find { it.row == r && it.col == c }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(3.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                                            .border(1.dp, Color(0xFF334155).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (arrow != null && arrow.state != ArrowState.ESCAPED) {
                                            GridArrowItem(
                                                arrow = arrow,
                                                onTap = { handleArrowTap(arrow) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Subtle, Clean Credits at Bottom
            Text(
                text = "This game is made by Ubaid",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .testTag("author_credit")
            )
        }
    }

    // Victory Modal
    if (isLevelWon) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("grid_win_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Nani Face in celebration ring
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFFBBF24), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nani_avatar),
                            contentDescription = "Nani Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Shabash! Level $currentLevelNum Cleared! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tamam Teer (Arrows) kamyabi se escape kar gaye!",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Stars rating
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Completed in $movesCount moves",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isLevelWon = false
                            val next = if (currentLevelNum >= 10) 1 else currentLevelNum + 1
                            onLevelAdvanced(next)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("grid_next_level_button")
                    ) {
                        Text(
                            text = if (currentLevelNum >= 10) "Play Again From Level 1" else "Next Level",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { resetLevel() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(text = "Replay Level", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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
}

@Composable
private fun GridArrowItem(
    arrow: GridArrow,
    onTap: () -> Unit
) {
    // Sliding offset calculation
    val slideDistancePx = 180f
    val slideX = when (arrow.direction) {
        Direction.LEFT -> -arrow.slideProgress * slideDistancePx
        Direction.RIGHT -> arrow.slideProgress * slideDistancePx
        else -> 0f
    }
    val slideY = when (arrow.direction) {
        Direction.UP -> -arrow.slideProgress * slideDistancePx
        Direction.DOWN -> arrow.slideProgress * slideDistancePx
        else -> 0f
    }

    // Bump shake calculation
    val bumpMagnitude = sin(arrow.bumpProgress * PI.toFloat()) * 18f
    val shakeMagnitude = sin(arrow.bumpProgress * 16f) * (1f - arrow.bumpProgress) * 12f

    val bumpX = (arrow.direction.dx * bumpMagnitude) + (-arrow.direction.dy * shakeMagnitude)
    val bumpY = (arrow.direction.dy * bumpMagnitude) + (arrow.direction.dx * shakeMagnitude)

    val totalOffsetX = slideX + bumpX
    val totalOffsetY = slideY + bumpY

    val angle = when (arrow.direction) {
        Direction.UP -> 0f
        Direction.RIGHT -> 90f
        Direction.DOWN -> 180f
        Direction.LEFT -> 270f
    }

    val arrowColor = when {
        arrow.state == ArrowState.COLLIDING -> Color(0xFFEF4444)
        arrow.isHinted -> Color(0xFFFBBF24)
        else -> arrow.color
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(totalOffsetX.roundToInt(), totalOffsetY.roundToInt()) }
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        arrowColor.copy(alpha = 0.28f),
                        Color(0xFF1E293B)
                    )
                )
            )
            .border(
                width = if (arrow.isHinted) 2.dp else 1.2.dp,
                color = arrowColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onTap() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "Arrow ${arrow.direction.name}",
            tint = arrowColor,
            modifier = Modifier
                .fillMaxSize(0.65f)
                .rotate(angle)
        )
    }
}
