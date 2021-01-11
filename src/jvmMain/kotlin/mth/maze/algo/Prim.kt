package mth.maze.algo

import mth.maze.Maze
import mth.maze.Maze.Companion.EAST
import mth.maze.Maze.Companion.NORTH
import mth.maze.Maze.Companion.SOUTH
import mth.maze.Maze.Companion.WEST
import mth.maze.MazeRegion
import mth.maze.algo.MazeGenerator.Companion.DX
import mth.maze.algo.MazeGenerator.Companion.DY
import java.awt.Point
import java.util.*
import kotlin.collections.ArrayList

class Prim : MazeGenerator {

    @Suppress("PrivatePropertyName")
    private val VISITED = "visited"
    lateinit var maze: Maze


    private fun cellHash(x: Int, y: Int, maze: Maze): Int = y * maze.width + x

    private fun fromHashToCell(hashCode: Int, maze: Maze) =
        Point(hashCode % maze.width, hashCode / maze.width)

    var frontierDimension = 0

    override fun generate(mazeRegion: MazeRegion, maze: Maze): Maze {
        // mark all cells as unvisited
        for (i in 0 until maze.width)
            for (j in 0 until maze.height)
                maze.cellAt(i, j)[VISITED] = false

        frontierDimension = Int.MIN_VALUE

        // choose randomly a point in the grid
        val startPoint = Point(random.nextInt(maze.width), random.nextInt(maze.height))
        val frontier = TreeSet<Int>()

        // mark the starting cell as visited and append its neighbors to the frontier
        with(startPoint) {
            maze.cellAt(x, y)[VISITED] = true
            neighborsPartition(x, y, mazeRegion, maze).second.forEach {
                frontier.add(cellHash(x + DX[it]!!, y + DY[it]!!, maze))
            }
        }

        while (frontier.isNotEmpty()) {
            // pick a cell from the frontier
            val hash = if (random.nextInt(2) == 0) frontier.pollFirst()!! else frontier.pollLast()

            // convert hash to coordinates and mark the cell as visited
            val cell = fromHashToCell(hash, maze)
            maze.cellAt(cell.x, cell.y)[VISITED] = true

            val (visited, unvisited) = neighborsPartition(cell.x, cell.y, mazeRegion, maze)
            // System.out.println("unvisited = $unvisited")
            // System.out.println("visited = $visited")

            carvePassage(cell.x, cell.y, visited[random.nextInt(visited.size)], maze)

            // update the frontier with the unvisited neighbors
            unvisited.forEach {
                frontier.add(cellHash(cell.x + DX[it]!!, cell.y + DY[it]!!, maze))
                // System.out.println(java.awt.Point(cell.x + DX[it]!!, cell.y + DY[it]!!))
                // System.out.println(cellHash(cell.x + DX[it]!!, cell.y + DY[it]!!, maze))
            }

            frontierDimension = maxOf(frontierDimension, frontier.size)
        }

        return maze
    }

    private fun neighborsPartition(
        X: Int,
        Y: Int,
        region: MazeRegion,
        maze: Maze
    ): Pair<List<Int>, List<Int>> {
        val directionsAllowed = mutableSetOf(NORTH, SOUTH, EAST, WEST)
        val visitedDir = ArrayList<Int>(4)
        val unvisitedDir = ArrayList<Int>(4)

        with(region) {
            if (X - x == 0) directionsAllowed.remove(WEST)
            if (Y - y == 0) directionsAllowed.remove(NORTH)
            if (X - x == width - 1) directionsAllowed.remove(EAST)
            if (Y - y == height - 1) directionsAllowed.remove(SOUTH)

            directionsAllowed.forEach {
                val visited = maze.cellAt(X + DX[it]!!, Y + DY[it]!!)[VISITED] == true

                if (visited) visitedDir.add(it) else unvisitedDir.add(it)
            }
        }

        return Pair(visitedDir, unvisitedDir)
    }

    /**
     * Search for the available moves around the cell identified by the absolute coordinates [X] and
     * [Y]
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
            if (X - x == 0) directionsAllowed.remove(WEST)
            if (Y - y == 0) directionsAllowed.remove(NORTH)
            if (X - x == width - 1) directionsAllowed.remove(EAST)
            if (Y - y == height - 1) directionsAllowed.remove(SOUTH)

            directionsAllowed.forEach {
                val visited = maze.cellAt(X + DX[it]!!, Y + DY[it]!!)[VISITED] == true

                if (!visited == searchForNeighborsUnvisited) directions.add(it)
            }
        }

        return directions
    }
}
