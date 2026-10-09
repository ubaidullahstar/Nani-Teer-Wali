package com.example.game.model

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

enum class Direction(val dx: Int, val dy: Int, val angleDegrees: Float) {
    UP(0, -1, 270f),
    DOWN(0, 1, 90f),
    LEFT(-1, 0, 180f),
    RIGHT(1, 0, 0f);

    companion object {
        fun fromDelta(dx: Int, dy: Int): Direction {
            return when {
                abs(dx) > abs(dy) -> if (dx > 0) RIGHT else LEFT
                else -> if (dy > 0) DOWN else UP
            }
        }
    }
}

data class GridPoint(val x: Int, val y: Int) {
    operator fun plus(other: GridPoint): GridPoint = GridPoint(x + other.x, y + other.y)
    operator fun minus(other: GridPoint): GridPoint = GridPoint(x - other.x, y - other.y)
}

enum class ArrowState {
    IDLE,
    COLLIDING,
    ESCAPING,
    ESCAPED
}

data class ArrowSnake(
    val id: Int,
    val points: List<GridPoint>,
    val color: Color = Color(0xFF1E293B),
    val state: ArrowState = ArrowState.IDLE,
    val collisionProgress: Float = 0f,
    val escapeProgress: Float = 0f,
    val isHinted: Boolean = false,
    val collisionTarget: GridPoint? = null
) {
    val head: GridPoint get() = points.last()
    val tail: GridPoint get() = points.first()

    val direction: Direction
        get() {
            if (points.size < 2) return Direction.RIGHT
            val prev = points[points.size - 2]
            val head = points.last()
            return Direction.fromDelta(head.x - prev.x, head.y - prev.y)
        }

    fun isPointOnSnake(p: GridPoint): Boolean {
        for (i in 0 until points.size - 1) {
            val a = points[i]
            val b = points[i + 1]
            if (a.x == b.x && p.x == a.x) {
                val minY = minOf(a.y, b.y)
                val maxY = maxOf(a.y, b.y)
                if (p.y in minY..maxY) return true
            } else if (a.y == b.y && p.y == a.y) {
                val minX = minOf(a.x, b.x)
                val maxX = maxOf(a.x, b.x)
                if (p.x in minX..maxX) return true
            }
        }
        return false
    }
}

data class GameLevel(
    val levelNumber: Int,
    val gridWidth: Int,
    val gridHeight: Int,
    val snakes: List<ArrowSnake>,
    val name: String = "Level $levelNumber"
)

enum class ThemeMode(val displayName: String) {
    CLASSIC("Classic"),      // Clean white background, crisp dark arrows (as in viral screenshot)
    DARK_NEON("Cyber Neon"), // Dark slate background, glowing neon arrows
    PASTEL("Candy Pastel"),  // Soft cream background, vibrant candy arrows
    MINIMAL("Monochrome")    // High contrast black & white
}

sealed class EscapeCheckResult {
    object Clear : EscapeCheckResult()
    data class Blocked(
        val blockerId: Int,
        val collisionPoint: GridPoint,
        val distance: Int
    ) : EscapeCheckResult()
}
