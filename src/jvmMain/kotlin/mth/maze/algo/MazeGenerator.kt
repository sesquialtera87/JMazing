package mth.jmazex.algo

import mth.jmazex.app.Maze
import mth.jmazex.app.Point
import java.util.*
import javax.swing.SwingConstants.*

interface MazeGenerator {

    val random: Random
        get() = Random()

    fun generate(xStart: Int, yStart: Int, width: Int, height: Int, maze: Maze): Maze

    /**
     * Used during the maze generation in a selection. Rebuild all the wall for the cell,
     * except the walls corresponding to the selection boundary
     * @param maze
     * @param x The horizontal coordinate of the cell
     * @param y The vertical coordinate of the cell
     */
    fun rebuildWalls(maze: Maze, x: Int, y: Int, selectionStart: Point, selectionSize: Point) {
        val (xStart, yStart) = selectionStart
        val (width, height) = selectionSize

        with(maze.cellAt(x + xStart, y + yStart)) {
            if (x > 0 && x < width - 1) {
                walls[WEST] = true
                walls[EAST] = true
            } else if (x == 0)
                walls[EAST] = true
            else if (x == width - 1)
                walls[WEST] = true

            if (y > 0 && y < height - 1) {
                walls[NORTH] = true
                walls[SOUTH] = true
            } else if (y == 0)
                walls[SOUTH] = true
            else if (y == height - 1)
                walls[NORTH] = true
        }
    }

    companion object {
        val opposite: Map<Int, Int> = mapOf(NORTH to SOUTH, SOUTH to NORTH, WEST to EAST, EAST to WEST)
        val DX: Map<Int, Int>
            get() = mapOf(
                NORTH to 0,
                SOUTH to 0,
                EAST to +1,
                WEST to -1
            )
        val DY: Map<Int, Int>
            get() = mapOf(
                NORTH to -1,
                SOUTH to +1,
                EAST to 0,
                WEST to 0
            )
    }

}