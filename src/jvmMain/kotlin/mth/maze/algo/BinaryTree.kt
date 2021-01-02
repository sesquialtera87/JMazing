package mth.jmazex.algo

import mth.jmazex.app.Maze
import mth.jmazex.app.Point
import java.util.*
import javax.swing.SwingConstants.*

class BinaryTree(D1: Int = NORTH, D2: Int = WEST) : MazeGenerator {

    private val directions = arrayOf(D1, D2)
    private val opposites = mapOf(D1 to D2, D2 to D1)
    lateinit var grid: Array<Array<Maze.Cell>>

    private fun passageAllowed(x: Int, y: Int, nx: Int, ny: Int, direction: Int) =
        when (direction) {
            NORTH -> y > 0
            EAST -> x < nx - 1
            WEST -> x > 0
            SOUTH -> y < ny - 1
            else -> false
        }

    private fun carvePassage(x: Int, y: Int, direction: Int) {
        when (direction) {
            NORTH -> {
                grid[x][y].walls[NORTH] = false
                grid[x][y - 1].walls[SOUTH] = false
            }
            SOUTH -> {
                grid[x][y].walls[SOUTH] = false
                grid[x][y + 1].walls[NORTH] = false
            }
            WEST -> {
                grid[x][y].walls[WEST] = false
                grid[x - 1][y].walls[EAST] = false
            }
            EAST -> {
                grid[x][y].walls[EAST] = false
                grid[x + 1][y].walls[WEST] = false
            }
        }
    }

    fun generate(nx: Int, ny: Int, maze: Maze): Maze {
        // all cell have the four wall
        grid = Array(nx) {
            Array(ny) {
                Maze.Cell(
                    mutableMapOf(
                        NORTH to true,
                        SOUTH to true,
                        EAST to true,
                        WEST to true
                    )
                )
            }
        }

        val maze = Maze(nx, ny)
        maze.grid = grid
        val rn = Random()

        for (i in 0 until nx)
            for (j in 0 until ny) {
                val index = rn.nextInt(2)
                val randomDirection = directions[index]

                if (passageAllowed(i, j, nx, ny, randomDirection))
                    carvePassage(i, j, randomDirection)
                else if (passageAllowed(i, j, nx, ny, opposites[randomDirection]!!))
                    carvePassage(i, j, opposites[randomDirection]!!)
            }

        return maze
    }


    override fun generate(
        xStart: Int,
        yStart: Int,
        width: Int,
        height: Int,
        maze: Maze
    ): Maze {

        val rn = Random()
        grid = maze.grid

        for (x in 0 until width)
            for (y in 0 until height) {
                rebuildWalls(maze, x, y, Point(xStart, yStart), Point(width, height))

                val index = rn.nextInt(2)
                val randomDirection = directions[index]

                if (passageAllowed(x, y, width, height, randomDirection))
                    carvePassage(x + xStart, y + yStart, randomDirection)
                else if (passageAllowed(x, y, width, height, opposites[randomDirection]!!))
                    carvePassage(x + xStart, y + yStart, opposites[randomDirection]!!)
            }

        return maze
    }
}