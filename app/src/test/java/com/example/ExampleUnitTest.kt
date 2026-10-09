package com.example

import com.example.game.grid.GridGameEngine
import com.example.game.grid.GridLevelRegistry
import com.example.game.logic.ArrowGameEngine
import com.example.game.logic.LevelRegistry
import com.example.game.model.EscapeCheckResult
import com.example.game.model.GridPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAllTenGridLevelsSolvabilityAndSpecs() {
        val expectedSpecs = listOf(
            Triple(1, 3, 2),  // Level 1: 3x3, 2 arrows
            Triple(2, 3, 3),  // Level 2: 3x3, 3 arrows
            Triple(3, 4, 4),  // Level 3: 4x4, 4 arrows
            Triple(4, 4, 5),  // Level 4: 4x4, 5 arrows
            Triple(5, 4, 6),  // Level 5: 4x4, 6 arrows
            Triple(6, 5, 6),  // Level 6: 5x5, 6 arrows
            Triple(7, 5, 7),  // Level 7: 5x5, 7 arrows
            Triple(8, 5, 8),  // Level 8: 5x5, 8 arrows
            Triple(9, 6, 9),  // Level 9: 6x6, 9 arrows
            Triple(10, 6, 10) // Level 10: 6x6, 10 arrows
        )

        for ((lvlId, expectedSize, expectedCount) in expectedSpecs) {
            val level = GridLevelRegistry.getLevel(lvlId)
            assertEquals("Level $lvlId should have grid size $expectedSize", expectedSize, level.gridSize)
            assertEquals("Level $lvlId should have $expectedCount arrows", expectedCount, level.arrows.size)

            val activeArrows = level.arrows.toMutableList()
            var step = 0
            while (activeArrows.isNotEmpty() && step < 50) {
                step++
                val freeArrow = GridGameEngine.findClearArrow(activeArrows, level.gridSize)
                assertNotNull("Normal Level $lvlId should never deadlock", freeArrow)
                activeArrows.removeIf { it.id == freeArrow!!.id }
            }
            assertTrue("Normal Level $lvlId should be 100% solvable", activeArrows.isEmpty())
        }
    }

    @Test
    fun testAllTenMazeLevelsSolvabilityAndZeroOverlap() {
        for (lvlId in 1..10) {
            val level = LevelRegistry.getLevel(lvlId)
            assertTrue("Maze Level $lvlId should have snakes", level.snakes.isNotEmpty())

            // Overlap check
            val snakeOccupiedPoints = level.snakes.map { snake ->
                val set = mutableSetOf<GridPoint>()
                for (i in 0 until snake.points.size - 1) {
                    val a = snake.points[i]
                    val b = snake.points[i + 1]
                    if (a.x == b.x) {
                        val minY = minOf(a.y, b.y)
                        val maxY = maxOf(a.y, b.y)
                        for (y in minY..maxY) set.add(GridPoint(a.x, y))
                    } else if (a.y == b.y) {
                        val minX = minOf(a.x, b.x)
                        val maxX = maxOf(a.x, b.x)
                        for (x in minX..maxX) set.add(GridPoint(x, a.y))
                    }
                }
                snake.id to set
            }

            for (i in snakeOccupiedPoints.indices) {
                for (j in i + 1 until snakeOccupiedPoints.size) {
                    val (idA, setA) = snakeOccupiedPoints[i]
                    val (idB, setB) = snakeOccupiedPoints[j]
                    val overlap = setA.intersect(setB)
                    assertTrue("Maze Level $lvlId: Snakes $idA & $idB overlap at $overlap", overlap.isEmpty())
                }
            }

            // Solvability check
            val active = level.snakes.toMutableList()
            var step = 0
            while (active.isNotEmpty() && step < 60) {
                step++
                val free = ArrowGameEngine.findClearSnake(active, level.gridWidth, level.gridHeight)
                assertNotNull("Maze Level $lvlId should never deadlock; remaining: ${active.map { it.id }}", free)
                active.removeIf { it.id == free!!.id }
            }
            assertTrue("Maze Level $lvlId should be 100% solvable", active.isEmpty())
        }
    }
}
