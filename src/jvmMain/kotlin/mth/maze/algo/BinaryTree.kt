package mth.maze.algo


import mth.maze.Maze
import mth.maze.Maze.Companion.EAST
import mth.maze.Maze.Companion.NORTH
import mth.maze.Maze.Companion.SOUTH
import mth.maze.Maze.Companion.WEST
import mth.maze.MazeRegion

class BinaryTree(D1: Int = NORTH, D2: Int = WEST) : MazeGenerator {

    private val directions = arrayOf(D1, D2)
    private val binaryOpposite = mapOf(D1 to D2, D2 to D1)


    private fun passageAllowed(x: Int, y: Int, width: Int, height: Int, direction: Int) =
        when (direction) {
            NORTH -> y > 0
            EAST -> x < width - 1
            WEST -> x > 0
            SOUTH -> y < height - 1
            else -> false
        }

    override fun generate(mazeRegion: MazeRegion, maze: Maze): Maze {
        with(mazeRegion) {
            // iterate over the maze-region
            for (x in 0 until width)
                for (y in 0 until height) {
                    rebuildWalls(maze, x, y, mazeRegion)

                    val randomDirection = randomizedDirection()

                    if (passageAllowed(x, y, width, height, randomDirection))
                        carvePassage(x + mazeRegion.x, y + mazeRegion.y, randomDirection, maze)
                    else if (passageAllowed(x, y, width, height, binaryOpposite[randomDirection]!!))
                        carvePassage(x + mazeRegion.x, y + mazeRegion.y, binaryOpposite[randomDirection]!!, maze)
                }
        }

        return maze

    }

    private fun randomizedDirection(): Int {
        val index = random.nextInt(2)
        return directions[index]
    }
}