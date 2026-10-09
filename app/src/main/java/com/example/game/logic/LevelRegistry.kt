package com.example.game.logic

import androidx.compose.ui.graphics.Color
import com.example.game.model.ArrowSnake
import com.example.game.model.GameLevel
import com.example.game.model.GridPoint

object LevelRegistry {

    fun getLevel(levelNumber: Int): GameLevel {
        val clamped = levelNumber.coerceIn(1, 10)
        return when (clamped) {
            1 -> createMazeLevel1()
            2 -> createMazeLevel2()
            3 -> createMazeLevel3()
            4 -> createMazeLevel4()
            5 -> createMazeLevel5()
            6 -> createMazeLevel6()
            7 -> createMazeLevel7()
            8 -> createMazeLevel8()
            9 -> createMazeLevel9()
            10 -> createMazeLevel10()
            else -> createMazeLevel1()
        }
    }

    private fun createMazeLevel1(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(9, 6), GridPoint(6, 6), GridPoint(6, 8)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(4, 6), GridPoint(5, 6), GridPoint(5, 5), GridPoint(3, 5)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(4, 4), GridPoint(4, 3), GridPoint(2, 3)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(5, 4), GridPoint(5, 2)), Color.Black)
        )
        return GameLevel(1, 10, 10, snakes, "Maze Level 1: Intro Labyrinth")
    }

    private fun createMazeLevel2(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(6, 8), GridPoint(6, 9), GridPoint(9, 9)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(7, 5), GridPoint(7, 1)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(5, 9), GridPoint(1, 9)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(9, 5), GridPoint(8, 5), GridPoint(8, 6), GridPoint(10, 6)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(5, 3), GridPoint(5, 5), GridPoint(2, 5)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(5, 2), GridPoint(5, 1), GridPoint(2, 1), GridPoint(2, 2), GridPoint(1, 2)), Color.Black)
        )
        return GameLevel(2, 11, 11, snakes, "Maze Level 2: Double Hook Matrix")
    }

    private fun createMazeLevel3(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(4, 6), GridPoint(5, 6), GridPoint(5, 3)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(11, 9), GridPoint(8, 9), GridPoint(8, 10)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(9, 4), GridPoint(9, 5), GridPoint(8, 5), GridPoint(8, 1)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(4, 7), GridPoint(4, 11)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(3, 10), GridPoint(1, 10)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(3, 4), GridPoint(4, 4), GridPoint(4, 2)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(7, 5), GridPoint(7, 1)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(5, 7), GridPoint(6, 7), GridPoint(6, 1)), Color.Black)
        )
        return GameLevel(3, 12, 12, snakes, "Maze Level 3: Ring Blocker Loop")
    }

    private fun createMazeLevel4(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(12, 3), GridPoint(8, 3), GridPoint(8, 1)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(7, 3), GridPoint(5, 3), GridPoint(5, 5), GridPoint(4, 5), GridPoint(4, 3)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(2, 5), GridPoint(2, 2)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(4, 12), GridPoint(4, 11), GridPoint(6, 11), GridPoint(6, 12)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(12, 7), GridPoint(10, 7), GridPoint(10, 6), GridPoint(11, 6)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(10, 1), GridPoint(10, 2), GridPoint(11, 2)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(12, 10), GridPoint(10, 10), GridPoint(10, 11), GridPoint(11, 11), GridPoint(11, 12)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(7, 11), GridPoint(8, 11), GridPoint(8, 12)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(5, 8), GridPoint(5, 9), GridPoint(10, 9)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(4, 6), GridPoint(4, 7), GridPoint(3, 7)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(3, 6), GridPoint(3, 3)), Color.Black)
        )
        return GameLevel(4, 13, 13, snakes, "Maze Level 4: Corridor Weave")
    }

    private fun createMazeLevel5(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(5, 9), GridPoint(5, 11), GridPoint(6, 11), GridPoint(6, 13)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(14, 8), GridPoint(14, 3), GridPoint(13, 3), GridPoint(13, 2)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(1, 12), GridPoint(1, 10), GridPoint(3, 10), GridPoint(3, 13)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(6, 4), GridPoint(3, 4)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(3, 3), GridPoint(3, 2), GridPoint(1, 2)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(5, 7), GridPoint(7, 7), GridPoint(7, 2)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(11, 13), GridPoint(10, 13), GridPoint(10, 12), GridPoint(12, 12), GridPoint(12, 13)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(8, 6), GridPoint(8, 5), GridPoint(9, 5), GridPoint(9, 3)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(11, 9), GridPoint(14, 9)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(4, 6), GridPoint(4, 8), GridPoint(3, 8)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(4, 9), GridPoint(2, 9), GridPoint(2, 8), GridPoint(1, 8)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(12, 3), GridPoint(10, 3), GridPoint(10, 1)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(3, 6), GridPoint(1, 6)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(9, 14), GridPoint(9, 11), GridPoint(14, 11)), Color.Black)
        )
        return GameLevel(5, 15, 15, snakes, "Maze Level 5: Pocket Matrix")
    }

    private fun createMazeLevel6(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(4, 6), GridPoint(5, 6), GridPoint(5, 4), GridPoint(7, 4), GridPoint(7, 3)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(15, 10), GridPoint(14, 10), GridPoint(14, 7), GridPoint(15, 7)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(13, 9), GridPoint(12, 9), GridPoint(12, 14)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(15, 3), GridPoint(14, 3), GridPoint(14, 1)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(6, 3), GridPoint(3, 3)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(13, 3), GridPoint(12, 3), GridPoint(12, 4), GridPoint(13, 4)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(3, 4), GridPoint(3, 5), GridPoint(2, 5)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(14, 11), GridPoint(14, 14)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(2, 13), GridPoint(2, 12), GridPoint(7, 12), GridPoint(7, 13)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(10, 4), GridPoint(10, 6), GridPoint(11, 6), GridPoint(11, 1)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(8, 12), GridPoint(8, 10), GridPoint(2, 10)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(8, 13), GridPoint(11, 13), GridPoint(11, 15)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(3, 6), GridPoint(3, 8), GridPoint(2, 8)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(10, 1), GridPoint(10, 2), GridPoint(9, 2), GridPoint(9, 1)), Color.Black),
            ArrowSnake(15, listOf(GridPoint(2, 15), GridPoint(5, 15), GridPoint(5, 14), GridPoint(3, 14)), Color.Black),
            ArrowSnake(16, listOf(GridPoint(3, 13), GridPoint(6, 13), GridPoint(6, 15)), Color.Black),
            ArrowSnake(17, listOf(GridPoint(1, 2), GridPoint(4, 2), GridPoint(4, 1)), Color.Black)
        )
        return GameLevel(6, 16, 16, snakes, "Maze Level 6: Spiral Shield Labyrinth")
    }

    private fun createMazeLevel7(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(10, 5), GridPoint(10, 6), GridPoint(11, 6), GridPoint(11, 3)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(5, 14), GridPoint(2, 14)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(12, 2), GridPoint(12, 5), GridPoint(15, 5)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(1, 9), GridPoint(1, 15), GridPoint(2, 15), GridPoint(2, 16)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(14, 14), GridPoint(14, 16), GridPoint(10, 16), GridPoint(10, 17)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(4, 1), GridPoint(4, 2), GridPoint(3, 2)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(1, 6), GridPoint(2, 6), GridPoint(2, 7), GridPoint(1, 7)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(5, 15), GridPoint(5, 17)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(7, 4), GridPoint(7, 1)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(17, 1), GridPoint(14, 1), GridPoint(14, 2), GridPoint(15, 2)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(15, 13), GridPoint(15, 14), GridPoint(17, 14)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(6, 5), GridPoint(8, 5), GridPoint(8, 2)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(4, 17), GridPoint(4, 16), GridPoint(3, 16), GridPoint(3, 17)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(1, 5), GridPoint(2, 5), GridPoint(2, 2)), Color.Black),
            ArrowSnake(15, listOf(GridPoint(17, 10), GridPoint(16, 10), GridPoint(16, 8), GridPoint(17, 8)), Color.Black),
            ArrowSnake(16, listOf(GridPoint(10, 10), GridPoint(13, 10), GridPoint(13, 11), GridPoint(15, 11)), Color.Black),
            ArrowSnake(17, listOf(GridPoint(13, 3), GridPoint(16, 3), GridPoint(16, 4), GridPoint(17, 4)), Color.Black),
            ArrowSnake(18, listOf(GridPoint(15, 15), GridPoint(17, 15)), Color.Black),
            ArrowSnake(19, listOf(GridPoint(8, 13), GridPoint(9, 13), GridPoint(9, 15)), Color.Black),
            ArrowSnake(20, listOf(GridPoint(12, 6), GridPoint(15, 6)), Color.Black)
        )
        return GameLevel(7, 18, 18, snakes, "Maze Level 7: Multi-Tier Web")
    }

    private fun createMazeLevel8(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(6, 18), GridPoint(4, 18), GridPoint(4, 16), GridPoint(3, 16)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(2, 15), GridPoint(2, 18)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(19, 19), GridPoint(19, 17), GridPoint(18, 17), GridPoint(18, 18)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(17, 15), GridPoint(17, 19)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(11, 4), GridPoint(7, 4), GridPoint(7, 3)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(7, 1), GridPoint(7, 2), GridPoint(3, 2)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(4, 8), GridPoint(6, 8), GridPoint(6, 9), GridPoint(3, 9)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(15, 6), GridPoint(16, 6), GridPoint(16, 4), GridPoint(17, 4), GridPoint(17, 3)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(13, 4), GridPoint(13, 7), GridPoint(17, 7)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(7, 6), GridPoint(5, 6), GridPoint(5, 4), GridPoint(3, 4)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(17, 14), GridPoint(17, 13), GridPoint(18, 13)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(2, 7), GridPoint(2, 3)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(7, 11), GridPoint(8, 11), GridPoint(8, 10), GridPoint(3, 10)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(2, 10), GridPoint(2, 8), GridPoint(1, 8)), Color.Black),
            ArrowSnake(15, listOf(GridPoint(10, 12), GridPoint(16, 12), GridPoint(16, 11), GridPoint(17, 11)), Color.Black),
            ArrowSnake(16, listOf(GridPoint(5, 17), GridPoint(7, 17), GridPoint(7, 18)), Color.Black),
            ArrowSnake(17, listOf(GridPoint(12, 17), GridPoint(10, 17), GridPoint(10, 18)), Color.Black),
            ArrowSnake(18, listOf(GridPoint(11, 1), GridPoint(11, 3), GridPoint(9, 3), GridPoint(9, 2)), Color.Black),
            ArrowSnake(19, listOf(GridPoint(15, 5), GridPoint(14, 5), GridPoint(14, 3)), Color.Black),
            ArrowSnake(20, listOf(GridPoint(19, 8), GridPoint(18, 8), GridPoint(18, 1)), Color.Black),
            ArrowSnake(21, listOf(GridPoint(3, 11), GridPoint(3, 12), GridPoint(2, 12)), Color.Black),
            ArrowSnake(22, listOf(GridPoint(5, 16), GridPoint(5, 15), GridPoint(4, 15), GridPoint(4, 14), GridPoint(1, 14)), Color.Black),
            ArrowSnake(23, listOf(GridPoint(15, 17), GridPoint(13, 17), GridPoint(13, 18), GridPoint(12, 18), GridPoint(12, 19)), Color.Black)
        )
        return GameLevel(8, 20, 20, snakes, "Maze Level 8: Pinwheel Fortress")
    }

    private fun createMazeLevel9(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(11, 18), GridPoint(14, 18), GridPoint(14, 19)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(17, 19), GridPoint(17, 15), GridPoint(19, 15)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(19, 11), GridPoint(19, 9), GridPoint(21, 9)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(8, 5), GridPoint(10, 5), GridPoint(10, 3)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(10, 18), GridPoint(6, 18), GridPoint(6, 19)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(3, 7), GridPoint(5, 7), GridPoint(5, 8), GridPoint(3, 8)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(4, 9), GridPoint(4, 10), GridPoint(3, 10)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(19, 14), GridPoint(20, 14), GridPoint(20, 12), GridPoint(21, 12)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(3, 20), GridPoint(3, 17), GridPoint(1, 17)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(1, 2), GridPoint(4, 2), GridPoint(4, 4), GridPoint(2, 4)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(4, 18), GridPoint(5, 18), GridPoint(5, 21)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(17, 2), GridPoint(17, 5), GridPoint(16, 5), GridPoint(16, 2)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(1, 14), GridPoint(3, 14), GridPoint(3, 15), GridPoint(2, 15)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(8, 1), GridPoint(8, 4), GridPoint(5, 4), GridPoint(5, 3)), Color.Black),
            ArrowSnake(15, listOf(GridPoint(2, 7), GridPoint(2, 10), GridPoint(1, 10)), Color.Black),
            ArrowSnake(16, listOf(GridPoint(15, 7), GridPoint(20, 7), GridPoint(20, 6), GridPoint(21, 6)), Color.Black),
            ArrowSnake(17, listOf(GridPoint(4, 13), GridPoint(5, 13), GridPoint(5, 12), GridPoint(2, 12)), Color.Black),
            ArrowSnake(18, listOf(GridPoint(2, 18), GridPoint(2, 20)), Color.Black),
            ArrowSnake(19, listOf(GridPoint(4, 5), GridPoint(4, 6), GridPoint(3, 6)), Color.Black),
            ArrowSnake(20, listOf(GridPoint(20, 1), GridPoint(20, 4), GridPoint(18, 4), GridPoint(18, 3)), Color.Black),
            ArrowSnake(21, listOf(GridPoint(5, 17), GridPoint(5, 16), GridPoint(3, 16)), Color.Black),
            ArrowSnake(22, listOf(GridPoint(11, 17), GridPoint(16, 17), GridPoint(16, 19)), Color.Black),
            ArrowSnake(23, listOf(GridPoint(9, 21), GridPoint(9, 20), GridPoint(13, 20), GridPoint(13, 21)), Color.Black),
            ArrowSnake(24, listOf(GridPoint(19, 18), GridPoint(20, 18), GridPoint(20, 19), GridPoint(21, 19)), Color.Black),
            ArrowSnake(25, listOf(GridPoint(13, 9), GridPoint(18, 9), GridPoint(18, 8), GridPoint(20, 8)), Color.Black)
        )
        return GameLevel(9, 22, 22, snakes, "Maze Level 9: Mega Tangled Web")
    }

    private fun createMazeLevel10(): GameLevel {
        val snakes = listOf(
            ArrowSnake(1, listOf(GridPoint(20, 21), GridPoint(20, 20), GridPoint(19, 20), GridPoint(19, 21)), Color.Black),
            ArrowSnake(2, listOf(GridPoint(14, 6), GridPoint(18, 6), GridPoint(18, 3)), Color.Black),
            ArrowSnake(3, listOf(GridPoint(5, 20), GridPoint(5, 18), GridPoint(6, 18), GridPoint(6, 19)), Color.Black),
            ArrowSnake(4, listOf(GridPoint(4, 6), GridPoint(4, 3)), Color.Black),
            ArrowSnake(5, listOf(GridPoint(2, 15), GridPoint(2, 18), GridPoint(4, 18), GridPoint(4, 21)), Color.Black),
            ArrowSnake(6, listOf(GridPoint(20, 18), GridPoint(20, 14), GridPoint(21, 14)), Color.Black),
            ArrowSnake(7, listOf(GridPoint(20, 12), GridPoint(20, 10), GridPoint(21, 10)), Color.Black),
            ArrowSnake(8, listOf(GridPoint(4, 8), GridPoint(1, 8)), Color.Black),
            ArrowSnake(9, listOf(GridPoint(13, 4), GridPoint(11, 4), GridPoint(11, 3)), Color.Black),
            ArrowSnake(10, listOf(GridPoint(18, 2), GridPoint(16, 2), GridPoint(16, 1)), Color.Black),
            ArrowSnake(11, listOf(GridPoint(2, 9), GridPoint(2, 12), GridPoint(3, 12), GridPoint(3, 14), GridPoint(2, 14)), Color.Black),
            ArrowSnake(12, listOf(GridPoint(12, 18), GridPoint(15, 18), GridPoint(15, 19)), Color.Black),
            ArrowSnake(13, listOf(GridPoint(10, 18), GridPoint(10, 20), GridPoint(6, 20), GridPoint(6, 21)), Color.Black),
            ArrowSnake(14, listOf(GridPoint(19, 5), GridPoint(20, 5), GridPoint(20, 4), GridPoint(21, 4)), Color.Black),
            ArrowSnake(15, listOf(GridPoint(16, 9), GridPoint(16, 7), GridPoint(19, 7)), Color.Black),
            ArrowSnake(16, listOf(GridPoint(7, 6), GridPoint(7, 3)), Color.Black),
            ArrowSnake(17, listOf(GridPoint(16, 19), GridPoint(17, 19), GridPoint(17, 21)), Color.Black),
            ArrowSnake(18, listOf(GridPoint(2, 7), GridPoint(2, 6), GridPoint(1, 6)), Color.Black),
            ArrowSnake(19, listOf(GridPoint(18, 8), GridPoint(18, 9), GridPoint(19, 9)), Color.Black),
            ArrowSnake(20, listOf(GridPoint(10, 4), GridPoint(8, 4), GridPoint(8, 2)), Color.Black),
            ArrowSnake(21, listOf(GridPoint(11, 14), GridPoint(11, 20)), Color.Black),
            ArrowSnake(22, listOf(GridPoint(1, 3), GridPoint(1, 5), GridPoint(3, 5), GridPoint(3, 2)), Color.Black),
            ArrowSnake(23, listOf(GridPoint(2, 4), GridPoint(2, 1)), Color.Black),
            ArrowSnake(24, listOf(GridPoint(20, 1), GridPoint(20, 2), GridPoint(19, 2), GridPoint(19, 1)), Color.Black),
            ArrowSnake(25, listOf(GridPoint(18, 16), GridPoint(18, 19)), Color.Black),
            ArrowSnake(26, listOf(GridPoint(3, 19), GridPoint(2, 19), GridPoint(2, 20)), Color.Black),
            ArrowSnake(27, listOf(GridPoint(15, 5), GridPoint(15, 2)), Color.Black),
            ArrowSnake(28, listOf(GridPoint(14, 19), GridPoint(14, 21)), Color.Black)
        )
        return GameLevel(10, 22, 22, snakes, "Maze Level 10: Master Extreme Labyrinth")
    }


}
