package com.example.game.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.game.logic.ArrowGameEngine
import com.example.game.model.ArrowSnake
import com.example.game.model.ArrowState
import com.example.game.model.GridPoint
import com.example.game.model.ThemeMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ArrowCanvas(
    modifier: Modifier = Modifier,
    snakes: List<ArrowSnake>,
    gridWidth: Int,
    gridHeight: Int,
    theme: ThemeMode,
    onSnakeTapped: (ArrowSnake) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(snakes, gridWidth, gridHeight) {
                detectTapGestures { tapOffset ->
                    val canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                    val cellPixel = calculateCellPixel(canvasSize, gridWidth, gridHeight)
                    val origin = calculateOrigin(canvasSize, gridWidth, gridHeight, cellPixel)
                    val toPixel: (GridPoint) -> Offset = { p ->
                        Offset(
                            origin.x + (p.x + 0.5f) * cellPixel,
                            origin.y + (p.y + 0.5f) * cellPixel
                        )
                    }
                    val tapped = ArrowGameEngine.findTappedSnake(
                        tapPos = tapOffset,
                        snakes = snakes,
                        toPixel = toPixel,
                        thresholdPx = cellPixel * 0.85f
                    )
                    tapped?.let { onSnakeTapped(it) }
                }
            }
    ) {
        val cellPixel = calculateCellPixel(size, gridWidth, gridHeight)
        val origin = calculateOrigin(size, gridWidth, gridHeight, cellPixel)
        val toPixel: (GridPoint) -> Offset = { p ->
            Offset(
                origin.x + (p.x + 0.5f) * cellPixel,
                origin.y + (p.y + 0.5f) * cellPixel
            )
        }

        // Sleek, clean stroke width matching the viral game screenshot
        val strokeWidth = (cellPixel * 0.16f).coerceIn(6f, 13f)
        val cornerRadius = cellPixel * 0.30f

        for (snake in snakes) {
            if (snake.state == ArrowState.ESCAPED) continue

            val baseColor = when (theme) {
                ThemeMode.CLASSIC -> Color(0xFF18181B) // Deep slate black (like screenshot)
                ThemeMode.MINIMAL -> Color.Black
                ThemeMode.DARK_NEON -> snake.color
                ThemeMode.PASTEL -> snake.color
            }

            when (snake.state) {
                ArrowState.IDLE -> {
                    drawIdleSnake(
                        snake = snake,
                        toPixel = toPixel,
                        strokeWidth = strokeWidth,
                        baseColor = baseColor,
                        cellPixel = cellPixel,
                        cornerRadius = cornerRadius,
                        isHinted = snake.isHinted,
                        hintScale = if (snake.isHinted) hintPulse else 1f
                    )
                }
                ArrowState.COLLIDING -> {
                    drawCollidingSnake(
                        snake = snake,
                        toPixel = toPixel,
                        strokeWidth = strokeWidth,
                        cellPixel = cellPixel,
                        cornerRadius = cornerRadius
                    )
                }
                ArrowState.ESCAPING -> {
                    drawEscapingSnake(
                        snake = snake,
                        toPixel = toPixel,
                        strokeWidth = strokeWidth,
                        baseColor = baseColor,
                        cellPixel = cellPixel,
                        cornerRadius = cornerRadius,
                        gridWidth = gridWidth,
                        gridHeight = gridHeight
                    )
                }
                ArrowState.ESCAPED -> {}
            }
        }
    }
}

private fun calculateCellPixel(size: Size, gridWidth: Int, gridHeight: Int): Float {
    val margin = 24f
    val availW = size.width - margin * 2
    val availH = size.height - margin * 2
    return min(availW / (gridWidth + 1), availH / (gridHeight + 1))
}

private fun calculateOrigin(size: Size, gridWidth: Int, gridHeight: Int, cellPixel: Float): Offset {
    val totalW = (gridWidth + 1) * cellPixel
    val totalH = (gridHeight + 1) * cellPixel
    return Offset(
        (size.width - totalW) / 2f,
        (size.height - totalH) / 2f
    )
}

/**
 * Builds a smoothly rounded path for orthogonal polyline segments.
 */
private fun buildSmoothPath(points: List<Offset>, cornerRadius: Float): Path {
    val path = Path()
    if (points.isEmpty()) return path
    if (points.size == 1) {
        path.moveTo(points[0].x, points[0].y)
        return path
    }

    path.moveTo(points[0].x, points[0].y)

    for (i in 1 until points.size - 1) {
        val prev = points[i - 1]
        val cur = points[i]
        val next = points[i + 1]

        val inDx = cur.x - prev.x
        val inDy = cur.y - prev.y
        val inLen = sqrt(inDx * inDx + inDy * inDy).coerceAtLeast(0.001f)
        val inUx = inDx / inLen
        val inUy = inDy / inLen

        val outDx = next.x - cur.x
        val outDy = next.y - cur.y
        val outLen = sqrt(outDx * outDx + outDy * outDy).coerceAtLeast(0.001f)
        val outUx = outDx / outLen
        val outUy = outDy / outLen

        val r = minOf(cornerRadius, inLen * 0.45f, outLen * 0.45f)

        // Point before corner
        val cornerStart = Offset(cur.x - inUx * r, cur.y - inUy * r)
        // Point after corner
        val cornerEnd = Offset(cur.x + outUx * r, cur.y + outUy * r)

        path.lineTo(cornerStart.x, cornerStart.y)
        path.quadraticTo(cur.x, cur.y, cornerEnd.x, cornerEnd.y)
    }

    path.lineTo(points.last().x, points.last().y)
    return path
}

private fun DrawScope.drawIdleSnake(
    snake: ArrowSnake,
    toPixel: (GridPoint) -> Offset,
    strokeWidth: Float,
    baseColor: Color,
    cellPixel: Float,
    cornerRadius: Float,
    isHinted: Boolean,
    hintScale: Float
) {
    val points = snake.points.map(toPixel)
    if (points.size < 2) return

    val color = if (isHinted) Color(0xFFF59E0B) else baseColor

    val path = buildSmoothPath(points, cornerRadius)

    // Glow halo if hinted
    if (isHinted) {
        drawPath(
            path = path,
            color = Color(0xFFFDE047).copy(alpha = 0.55f),
            style = Stroke(
                width = strokeWidth * 2.2f * hintScale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // Draw snake body path
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Draw Open Chevron Arrowhead (matching viral screenshot)
    val headPix = points.last()
    val prevPix = points[points.size - 2]
    val dx = headPix.x - prevPix.x
    val dy = headPix.y - prevPix.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)

    drawChevronArrowHead(
        tip = headPix,
        dirX = dx / len,
        dirY = dy / len,
        color = color,
        strokeWidth = strokeWidth,
        cellPixel = cellPixel
    )
}

private fun DrawScope.drawCollidingSnake(
    snake: ArrowSnake,
    toPixel: (GridPoint) -> Offset,
    strokeWidth: Float,
    cellPixel: Float,
    cornerRadius: Float
) {
    val p = snake.collisionProgress
    val shakeMag = sin(p * 24f) * (1f - p) * cellPixel * 0.22f
    val bumpMag = sin(p * PI.toFloat()) * cellPixel * 0.30f

    val dir = snake.direction
    val perpX = -dir.dy.toFloat() * shakeMag
    val perpY = dir.dx.toFloat() * shakeMag

    val nudgeX = dir.dx.toFloat() * bumpMag + perpX
    val nudgeY = dir.dy.toFloat() * bumpMag + perpY

    val points = snake.points.map { pt ->
        val orig = toPixel(pt)
        Offset(orig.x + nudgeX, orig.y + nudgeY)
    }

    val warningColor = Color(0xFFEF4444) // Bright red
    val path = buildSmoothPath(points, cornerRadius)

    drawPath(
        path = path,
        color = warningColor,
        style = Stroke(
            width = strokeWidth * 1.15f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    val headPix = points.last()
    val prevPix = points[points.size - 2]
    val dx = headPix.x - prevPix.x
    val dy = headPix.y - prevPix.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)

    drawChevronArrowHead(
        tip = headPix,
        dirX = dx / len,
        dirY = dy / len,
        color = warningColor,
        strokeWidth = strokeWidth * 1.15f,
        cellPixel = cellPixel
    )

    // Collision impact ring
    snake.collisionTarget?.let { targetGrid ->
        val collisionPixel = toPixel(targetGrid)
        val rippleRadius = cellPixel * (0.25f + p * 0.5f)
        val rippleAlpha = (1f - p).coerceIn(0f, 1f)
        drawCircle(
            color = Color(0xFFEF4444).copy(alpha = rippleAlpha),
            radius = rippleRadius,
            center = collisionPixel,
            style = Stroke(width = strokeWidth * 0.7f)
        )
    }
}

private fun DrawScope.drawEscapingSnake(
    snake: ArrowSnake,
    toPixel: (GridPoint) -> Offset,
    strokeWidth: Float,
    baseColor: Color,
    cellPixel: Float,
    cornerRadius: Float,
    gridWidth: Int,
    gridHeight: Int
) {
    val origPoints = snake.points.map(toPixel).toMutableList()
    if (origPoints.size < 2) return

    val dir = snake.direction
    val exitDistance = cellPixel * (maxOf(gridWidth, gridHeight) + 12)
    val headPix = origPoints.last()
    val exitPix = Offset(
        headPix.x + dir.dx * exitDistance,
        headPix.y + dir.dy * exitDistance
    )
    origPoints.add(exitPix)

    val segLens = mutableListOf<Float>()
    var totalCurveLen = 0f
    for (i in 0 until origPoints.size - 1) {
        val a = origPoints[i]
        val b = origPoints[i + 1]
        val d = sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y))
        segLens.add(d)
        totalCurveLen += d
    }

    var origBodyLen = 0f
    for (i in 0 until snake.points.size - 1) {
        val a = toPixel(snake.points[i])
        val b = toPixel(snake.points[i + 1])
        origBodyLen += sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y))
    }

    val progress = snake.escapeProgress.coerceIn(0f, 1f)
    val travel = progress * (origBodyLen + exitDistance)
    val sStart = travel
    val sEnd = travel + origBodyLen

    if (sStart >= totalCurveLen) return

    val (subPoints, tangentDir) = sampleSubcurve(origPoints, segLens, sStart, sEnd)
    if (subPoints.size < 2) return

    val path = buildSmoothPath(subPoints, cornerRadius)

    drawPath(
        path = path,
        color = baseColor,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    drawChevronArrowHead(
        tip = subPoints.last(),
        dirX = tangentDir.x,
        dirY = tangentDir.y,
        color = baseColor,
        strokeWidth = strokeWidth,
        cellPixel = cellPixel
    )
}

private fun sampleSubcurve(
    points: List<Offset>,
    segLens: List<Float>,
    sStart: Float,
    sEnd: Float
): Pair<List<Offset>, Offset> {
    var accumulated = 0f
    val result = mutableListOf<Offset>()
    var lastTangent = Offset(1f, 0f)

    for (i in segLens.indices) {
        val segStartDist = accumulated
        val segEndDist = accumulated + segLens[i]
        val a = points[i]
        val b = points[i + 1]
        val segLen = segLens[i].coerceAtLeast(0.001f)
        val dir = Offset((b.x - a.x) / segLen, (b.y - a.y) / segLen)

        if (segEndDist >= sStart && segStartDist <= sEnd) {
            val localStart = (sStart - segStartDist).coerceIn(0f, segLen)
            val localEnd = (sEnd - segStartDist).coerceIn(0f, segLen)

            if (result.isEmpty()) {
                result.add(Offset(a.x + dir.x * localStart, a.y + dir.y * localStart))
            }

            if (segEndDist in sStart..sEnd) {
                result.add(b)
            } else if (localEnd < segLen) {
                result.add(Offset(a.x + dir.x * localEnd, a.y + dir.y * localEnd))
            }
            lastTangent = dir
        }

        accumulated = segEndDist
    }

    return Pair(result, lastTangent)
}

/**
 * Authentic Open Chevron / V-barb Arrowhead:
 * Two symmetrical wings extending backwards from the tip at 34 degrees,
 * drawn with the exact same stroke width, rounded caps, and no bulky notch.
 * Perfectly replicates the viral TikTok arrow maze game screenshot!
 */
private fun DrawScope.drawChevronArrowHead(
    tip: Offset,
    dirX: Float,
    dirY: Float,
    color: Color,
    strokeWidth: Float,
    cellPixel: Float
) {
    val wingLen = (cellPixel * 0.44f).coerceIn(strokeWidth * 2.2f, strokeWidth * 3.6f)
    val angleRad = 34f * (PI.toFloat() / 180f)
    val cosA = cos(angleRad)
    val sinA = sin(angleRad)

    // Left wing vector rotated by +angle backwards from tip
    val leftDx = -(dirX * cosA - dirY * sinA) * wingLen
    val leftDy = -(dirY * cosA + dirX * sinA) * wingLen

    // Right wing vector rotated by -angle backwards from tip
    val rightDx = -(dirX * cosA + dirY * sinA) * wingLen
    val rightDy = -(dirY * cosA - dirX * sinA) * wingLen

    val leftWing = Offset(tip.x + leftDx, tip.y + leftDy)
    val rightWing = Offset(tip.x + rightDx, tip.y + rightDy)

    val chevronPath = Path().apply {
        moveTo(leftWing.x, leftWing.y)
        lineTo(tip.x, tip.y)
        lineTo(rightWing.x, rightWing.y)
    }

    drawPath(
        path = chevronPath,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
