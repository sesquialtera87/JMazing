package mth.maze.algo


import mth.maze.Maze
import mth.maze.Maze.Companion.EAST
import mth.maze.Maze.Companion.NORTH
import mth.maze.Maze.Companion.SOUTH
import mth.maze.Maze.Companion.WEST
import mth.maze.MazeRegion
import mth.maze.algo.MazeGenerator.Companion.DX
import mth.maze.algo.MazeGenerator.Companion.DY
import mth.maze.component1
import mth.maze.component2
import java.awt.Point


class HuntAndKill : MazeGenerator {

    private val VISITED = "visited"
    private var visitedCounter = 0


    override fun generate(mazeRegion: MazeRegion, maze: Maze): Maze {
        // mark all cells as not visited
        for (x in 0 until mazeRegion.width)
            for (y in 0 until mazeRegion.height) {
                maze.cellAt(x + mazeRegion.x, y + mazeRegion.y)[VISITED] = false
            }

        this.visitedCounter = 0

        var walkStart =
            Point(mazeRegion.x + random.nextInt(mazeRegion.width), mazeRegion.y + random.nextInt(mazeRegion.height))

        while (visitedCounter < mazeRegion.width * mazeRegion.height) {
            randomWalk(walkStart.x, walkStart.y, mazeRegion, maze)
            val neighbor = hunting(mazeRegion, maze)

            if (neighbor == null) {
                break
            } else
                walkStart = Point(neighbor.x, neighbor.y)
        }

        return maze
    }

    private fun hunting(region: MazeRegion, maze: Maze): Point? {
        with(region) {
            for (i in 0 until width)
                for (j in 0 until height) {
                    val X = i + x
                    val Y = j + y

                    if (maze.cellAt(X, Y)[VISITED] == false) {
                        // search for visited neighborhoods
                        val directions = getAvailableDirections(X, Y, region, maze, false)

                        if (directions.isNotEmpty()) {
                            carvePassage(X, Y, directions[0], maze)
                            return Point(X, Y)
                        }
                    }
                }
        }

        return null
    }

    /**
     * Search for the available moves around the cell identified by the absolute coordinates [X] and [Y]
     * @param X The absolute horizontal coordinate of the cell
     * @param Y The absolute vertical coordinate of the cell
     * @param searchForNeighborsUnvisited A flag indicating the search goal
     */
    private fun getAvailableDirections(
        X: Int,
        Y: Int,
        region: MazeRegion,
        maze: Maze,
        searchForNeighborsUnvisited: Boolean = true
    ): List<Int> {
        val directionsAllowed = mutableSetOf(NORTH, SOUTH, EAST, WEST)
        val directions = mutableListOf<Int>()

        with(region) {
            if (X - x == 0)
                directionsAllowed.remove(WEST)
            if (Y - y == 0)
                directionsAllowed.remove(NORTH)
            if (X - x == width - 1)
                directionsAllowed.remove(EAST)
            if (Y - y == height - 1)
                directionsAllowed.remove(SOUTH)

            directionsAllowed.forEach {
                println(maze.cellAt(X + DX[it]!!, Y + DY[it]!!)[VISITED])
                val visited = maze.cellAt(X + DX[it]!!, Y + DY[it]!!)[VISITED] == true

                if (!visited == searchForNeighborsUnvisited)
                    directions.add(it)
            }
        }
        return directions
    }


    /**
     * Make a random walk, beginning from the absolute coordinates [fromX] e [fromY] until it reach a
     * dead end
     */
    private fun randomWalk(fromX: Int, fromY: Int, region: MazeRegion, maze: Maze) {
        var currentX = fromX
        var currentY = fromY
        var directions = getAvailableDirections(fromX, fromY, region, maze)
        maze.cellAt(fromX, fromY)[VISITED] = true
        visitedCounter++

        while (directions.isNotEmpty()) {
            val direction = directions[random.nextInt(directions.size)]
            val (x, y) = carvePassage(currentX, currentY, direction, maze)

            maze.cellAt(x, y)[VISITED] = true
            visitedCounter++

            currentX = x
            currentY = y

            directions = getAvailableDirections(x, y, region, maze)
        }
    }
}





