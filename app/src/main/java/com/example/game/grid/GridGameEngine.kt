package com.example.game.grid

import com.example.game.model.ArrowState

sealed class GridEscapeResult {
    object Clear : GridEscapeResult()
    data class Blocked(val blockerId: Int) : GridEscapeResult()
}

object GridGameEngine {

    fun checkEscape(
        arrow: GridArrow,
        allArrows: List<GridArrow>,
        gridSize: Int
    ): GridEscapeResult {
        val activeBlockers = allArrows.filter {
            it.id != arrow.id && (it.state == ArrowState.IDLE || it.state == ArrowState.COLLIDING)
        }

        var cr = arrow.row + arrow.direction.dy
        var cc = arrow.col + arrow.direction.dx

        while (cr in 0 until gridSize && cc in 0 until gridSize) {
            val blocker = activeBlockers.find { it.row == cr && it.col == cc }
            if (blocker != null) {
                return GridEscapeResult.Blocked(blocker.id)
            }
            cr += arrow.direction.dy
            cc += arrow.direction.dx
        }

        return GridEscapeResult.Clear
    }

    fun findClearArrow(
        allArrows: List<GridArrow>,
        gridSize: Int
    ): GridArrow? {
        val idleArrows = allArrows.filter { it.state == ArrowState.IDLE }
        return idleArrows.firstOrNull { checkEscape(it, allArrows, gridSize) is GridEscapeResult.Clear }
    }
}
