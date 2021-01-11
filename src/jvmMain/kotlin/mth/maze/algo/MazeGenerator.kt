package mth.maze.algo

import mth.maze.Maze
import mth.maze.Maze.Companion.EAST
import mth.maze.Maze.Companion.NORTH
import mth.maze.Maze.Companion.SOUTH
import mth.maze.Maze.Companion.WEST
import mth.maze.MazeRegion
import java.awt.Point
import java.util.*

interface MazeGenerator {

    val wallDestroyer: Boolean
        get() = true

    val random: Random
        get() = Random()

    fun generate(mazeRegion: MazeRegion, maze: Maze): Maze


    /**
     * Opens a passage from the cell (x,y) to the cell specified by [direction]
     */
    fun carvePassage(x: Int, y: Int, direction: Int, maze: Maze): Point {
        val destinationX = x + DX[direction]!!
        val destinationY = y + DY[direction]!!

        maze.cellAt(x, y).walls[direction] = false
        maze.cellAt(destinationX, destinationY).walls[opposite.getValue(direction)] = false

        return Point(destinationX, destinationY)
    }

    /**
     * Used during the maze generation in a selection. Rebuild all the wall for the cell,
     * except the walls corresponding to the selection boundary
     * @param maze
     * @param x The horizontal coordinate of the cell
     * @param y The vertical coordinate of the cell
     */
    fun rebuildWalls(maze: Maze, x: Int, y: Int, region: MazeRegion) {
        with(maze.cellAt(x + region.x, y + region.y)) {
            if (x > 0 && x < region.width - 1) {
                walls[WEST] = true
                walls[EAST] = true
            } else if (x == 0)
                walls[EAST] = true
            else if (x == region.width - 1)
                walls[WEST] = true

            if (y > 0 && y < region.height - 1) {
                walls[NORTH] = true
                walls[SOUTH] = true
            } else if (y == 0)
                walls[SOUTH] = true
            else if (y == region.height - 1)
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