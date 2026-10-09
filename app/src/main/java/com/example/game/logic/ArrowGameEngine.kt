package com.example.game.logic

import androidx.compose.ui.geometry.Offset
import com.example.game.model.ArrowSnake
import com.example.game.model.ArrowState
import com.example.game.model.EscapeCheckResult
import com.example.game.model.GridPoint
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object ArrowGameEngine {

    /**
     * Checks if a snake has an unobstructed ray forward from its head to the boundary.
     */
    fun checkEscape(
        snake: ArrowSnake,
        allSnakes: List<ArrowSnake>,
        gridWidth: Int,
        gridHeight: Int
    ): EscapeCheckResult {
        val dir = snake.direction
        val head = snake.head

        val activeBlockers = allSnakes.filter { other ->
            other.id != snake.id && (other.state == ArrowState.IDLE || other.state == ArrowState.COLLIDING)
        }

        var step = 1
        val maxBound = max(gridWidth, gridHeight) + 4
        while (step <= maxBound) {
            val checkPoint = GridPoint(head.x + dir.dx * step, head.y + dir.dy * step)

            // Check if checkPoint hits any other active snake
            for (blocker in activeBlockers) {
                if (blocker.isPointOnSnake(checkPoint)) {
                    return EscapeCheckResult.Blocked(
                        blockerId = blocker.id,
                        collisionPoint = checkPoint,
                        distance = step
                    )
                }
            }

            // If checkPoint is beyond the grid bounds, it has escaped safely!
            if (checkPoint.x < 0 || checkPoint.x > gridWidth || checkPoint.y < 0 || checkPoint.y > gridHeight) {
                return EscapeCheckResult.Clear
            }

            step++
        }

        return EscapeCheckResult.Clear
    }

    /**
     * Finds an unblocked snake that can escape right now. Used for hints.
     */
    fun findClearSnake(
        allSnakes: List<ArrowSnake>,
        gridWidth: Int,
        gridHeight: Int
    ): ArrowSnake? {
        val idleSnakes = allSnakes.filter { it.state == ArrowState.IDLE }
        for (snake in idleSnakes) {
            if (checkEscape(snake, allSnakes, gridWidth, gridHeight) is EscapeCheckResult.Clear) {
                return snake
            }
        }
        return null
    }

    /**
     * Calculates minimum distance from a point to a line segment.
     */
    fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
        val l2 = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)
        if (l2 == 0f) {
            val dx = p.x - a.x
            val dy = p.y - a.y
            return sqrt(dx * dx + dy * dy)
        }
        val t = max(0f, min(1f, ((p.x - a.x) * (b.x - a.x) + (p.y - a.y) * (b.y - a.y)) / l2))
        val projX = a.x + t * (b.x - a.x)
        val projY = a.y + t * (b.y - a.y)
        val dx = p.x - projX
        val dy = p.y - projY
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * Resolves which active snake was tapped within touch threshold.
     */
    fun findTappedSnake(
        tapPos: Offset,
        snakes: List<ArrowSnake>,
        toPixel: (GridPoint) -> Offset,
        thresholdPx: Float
    ): ArrowSnake? {
        val activeSnakes = snakes.filter { it.state == ArrowState.IDLE }
        var closestSnake: ArrowSnake? = null
        var closestDist = Float.MAX_VALUE

        for (snake in activeSnakes) {
            // Check each segment of the snake
            for (i in 0 until snake.points.size - 1) {
                val a = toPixel(snake.points[i])
                val b = toPixel(snake.points[i + 1])
                val d = distanceToSegment(tapPos, a, b)
                if (d < thresholdPx && d < closestDist) {
                    closestDist = d
                    closestSnake = snake
                }
            }
            // Also check head specifically with a little extra tolerance
            val headPix = toPixel(snake.head)
            val dx = tapPos.x - headPix.x
            val dy = tapPos.y - headPix.y
            val headDist = sqrt(dx * dx + dy * dy)
            if (headDist < thresholdPx * 1.3f && headDist < closestDist) {
                closestDist = headDist
                closestSnake = snake
            }
        }

        return closestSnake
    }
}
