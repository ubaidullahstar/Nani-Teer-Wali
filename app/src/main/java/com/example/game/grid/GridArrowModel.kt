package com.example.game.grid

import androidx.compose.ui.graphics.Color
import com.example.game.model.ArrowState
import com.example.game.model.Direction

data class GridArrow(
    val id: Int,
    val row: Int,
    val col: Int,
    val direction: Direction,
    val color: Color = Color(0xFF38BDF8), // Cyan/Sky neon accent
    val state: ArrowState = ArrowState.IDLE,
    val slideProgress: Float = 0f,
    val bumpProgress: Float = 0f,
    val isHinted: Boolean = false
)

data class GridLevel(
    val id: Int,
    val gridSize: Int,
    val arrows: List<GridArrow>,
    val title: String,
    val subtitle: String
)

object GridLevelRegistry {

    private val COLORS = listOf(
        Color(0xFF38BDF8), // Neon Sky
        Color(0xFF34D399), // Neon Emerald
        Color(0xFFFBBF24), // Neon Amber
        Color(0xFFF43F5E), // Neon Rose
        Color(0xFFA78BFA), // Neon Purple
        Color(0xFFFB923C), // Neon Orange
        Color(0xFF2DD4BF), // Neon Teal
        Color(0xFFE879F9), // Neon Fuchsia
        Color(0xFF60A5FA), // Neon Blue
        Color(0xFF4ADE80)  // Neon Lime
    )

    fun getLevel(levelNum: Int): GridLevel {
        val clamped = levelNum.coerceIn(1, 10)
        return when (clamped) {
            1 -> createLevel1()
            2 -> createLevel2()
            3 -> createLevel3()
            4 -> createLevel4()
            5 -> createLevel5()
            6 -> createLevel6()
            7 -> createLevel7()
            8 -> createLevel8()
            9 -> createLevel9()
            10 -> createLevel10()
            else -> createLevel1()
        }
    }

    // Level 1 (3x3 Grid, Easy): 2 arrows pointing towards open edges
    private fun createLevel1() = GridLevel(
        id = 1,
        gridSize = 3,
        arrows = listOf(
            GridArrow(1, 1, 2, Direction.RIGHT, COLORS[0]),
            GridArrow(2, 1, 0, Direction.RIGHT, COLORS[1])
        ),
        title = "Level 1: Open Paths",
        subtitle = "3x3 Grid • 2 Teer (Arrows)"
    )

    // Level 2 (3x3 Grid): 3 arrows requiring a simple 2-step sequence
    private fun createLevel2() = GridLevel(
        id = 2,
        gridSize = 3,
        arrows = listOf(
            GridArrow(1, 0, 1, Direction.UP, COLORS[0]),
            GridArrow(2, 2, 1, Direction.UP, COLORS[1]),
            GridArrow(3, 2, 0, Direction.RIGHT, COLORS[2])
        ),
        title = "Level 2: Sequence Step",
        subtitle = "3x3 Grid • 3 Teer (Arrows)"
    )

    // Level 3 (4x4 Grid): 4 arrows where one blocks another
    private fun createLevel3() = GridLevel(
        id = 3,
        gridSize = 4,
        arrows = listOf(
            GridArrow(1, 0, 2, Direction.UP, COLORS[0]),
            GridArrow(2, 3, 2, Direction.UP, COLORS[1]),
            GridArrow(3, 1, 3, Direction.RIGHT, COLORS[2]),
            GridArrow(4, 1, 0, Direction.RIGHT, COLORS[3])
        ),
        title = "Level 3: Blocker Logic",
        subtitle = "4x4 Grid • 4 Teer (Arrows)"
    )

    // Level 4 (4x4 Grid): 5 arrows with criss-crossing paths
    private fun createLevel4() = GridLevel(
        id = 4,
        gridSize = 4,
        arrows = listOf(
            GridArrow(1, 0, 1, Direction.UP, COLORS[0]),
            GridArrow(2, 2, 3, Direction.RIGHT, COLORS[1]),
            GridArrow(3, 2, 1, Direction.RIGHT, COLORS[2]),
            GridArrow(4, 3, 2, Direction.DOWN, COLORS[3]),
            GridArrow(5, 1, 2, Direction.DOWN, COLORS[4])
        ),
        title = "Level 4: Criss-Cross",
        subtitle = "4x4 Grid • 5 Teer (Arrows)"
    )

    // Level 5 (4x4 Grid): 6 interlocking arrows
    private fun createLevel5() = GridLevel(
        id = 5,
        gridSize = 4,
        arrows = listOf(
            GridArrow(1, 0, 0, Direction.LEFT, COLORS[0]),
            GridArrow(2, 0, 3, Direction.UP, COLORS[1]),
            GridArrow(3, 3, 3, Direction.RIGHT, COLORS[2]),
            GridArrow(4, 3, 0, Direction.DOWN, COLORS[3]),
            GridArrow(5, 1, 1, Direction.UP, COLORS[4]),
            GridArrow(6, 2, 2, Direction.RIGHT, COLORS[5])
        ),
        title = "Level 5: Interlocking Lock",
        subtitle = "4x4 Grid • 6 Teer (Arrows)"
    )

    // Level 6 (5x5 Grid, Intermediate): 6 arrows spread across a larger board
    private fun createLevel6() = GridLevel(
        id = 6,
        gridSize = 5,
        arrows = listOf(
            GridArrow(1, 0, 2, Direction.UP, COLORS[0]),
            GridArrow(2, 4, 2, Direction.DOWN, COLORS[1]),
            GridArrow(3, 2, 0, Direction.LEFT, COLORS[2]),
            GridArrow(4, 2, 4, Direction.RIGHT, COLORS[3]),
            GridArrow(5, 1, 2, Direction.UP, COLORS[4]),
            GridArrow(6, 3, 2, Direction.DOWN, COLORS[5])
        ),
        title = "Level 6: Wide Expansion",
        subtitle = "5x5 Grid • 6 Teer (Arrows)"
    )

    // Level 7 (5x5 Grid): 7 arrows requiring careful planning
    private fun createLevel7() = GridLevel(
        id = 7,
        gridSize = 5,
        arrows = listOf(
            GridArrow(1, 0, 1, Direction.UP, COLORS[0]),
            GridArrow(2, 4, 3, Direction.DOWN, COLORS[1]),
            GridArrow(3, 1, 4, Direction.RIGHT, COLORS[2]),
            GridArrow(4, 1, 1, Direction.RIGHT, COLORS[3]),
            GridArrow(5, 3, 0, Direction.LEFT, COLORS[4]),
            GridArrow(6, 3, 3, Direction.LEFT, COLORS[5]),
            GridArrow(7, 2, 2, Direction.UP, COLORS[6])
        ),
        title = "Level 7: Tactical Plan",
        subtitle = "5x5 Grid • 7 Teer (Arrows)"
    )

    // Level 8 (5x5 Grid): 8 dense arrows with tight clearances
    private fun createLevel8() = GridLevel(
        id = 8,
        gridSize = 5,
        arrows = listOf(
            GridArrow(1, 0, 2, Direction.UP, COLORS[0]),
            GridArrow(2, 4, 2, Direction.DOWN, COLORS[1]),
            GridArrow(3, 2, 0, Direction.LEFT, COLORS[2]),
            GridArrow(4, 2, 4, Direction.RIGHT, COLORS[3]),
            GridArrow(5, 1, 1, Direction.UP, COLORS[4]),
            GridArrow(6, 3, 3, Direction.DOWN, COLORS[5]),
            GridArrow(7, 1, 3, Direction.RIGHT, COLORS[6]),
            GridArrow(8, 3, 1, Direction.LEFT, COLORS[7])
        ),
        title = "Level 8: Dense Grid",
        subtitle = "5x5 Grid • 8 Teer (Arrows)"
    )

    // Level 9 (6x6 Grid, Advanced): 9 complex arrows
    private fun createLevel9() = GridLevel(
        id = 9,
        gridSize = 6,
        arrows = listOf(
            GridArrow(1, 0, 1, Direction.UP, COLORS[0]),
            GridArrow(2, 0, 4, Direction.UP, COLORS[1]),
            GridArrow(3, 5, 2, Direction.DOWN, COLORS[2]),
            GridArrow(4, 5, 5, Direction.DOWN, COLORS[3]),
            GridArrow(5, 2, 0, Direction.LEFT, COLORS[4]),
            GridArrow(6, 2, 4, Direction.LEFT, COLORS[5]),
            GridArrow(7, 4, 1, Direction.RIGHT, COLORS[6]),
            GridArrow(8, 4, 5, Direction.RIGHT, COLORS[7]),
            GridArrow(9, 3, 2, Direction.UP, COLORS[8])
        ),
        title = "Level 9: Grand Matrix",
        subtitle = "6x6 Grid • 9 Teer (Arrows)"
    )

    // Level 10 (6x6 Grid, Master Level): 10 challenging arrows requiring precise sequence
    private fun createLevel10() = GridLevel(
        id = 10,
        gridSize = 6,
        arrows = listOf(
            GridArrow(1, 0, 2, Direction.UP, COLORS[0]),
            GridArrow(2, 0, 5, Direction.RIGHT, COLORS[1]),
            GridArrow(3, 5, 0, Direction.LEFT, COLORS[2]),
            GridArrow(4, 5, 3, Direction.DOWN, COLORS[3]),
            GridArrow(5, 2, 2, Direction.UP, COLORS[4]),
            GridArrow(6, 3, 3, Direction.DOWN, COLORS[5]),
            GridArrow(7, 1, 4, Direction.RIGHT, COLORS[6]),
            GridArrow(8, 4, 1, Direction.LEFT, COLORS[7]),
            GridArrow(9, 2, 3, Direction.RIGHT, COLORS[8]),
            GridArrow(10, 3, 2, Direction.LEFT, COLORS[9])
        ),
        title = "Level 10: Master Escape",
        subtitle = "6x6 Grid • 10 Teer (Master Level)"
    )
}
