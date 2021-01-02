package mth.jmazex.algo

import mth.jmazex.algo.MazeGenerator.Companion.DX
import mth.jmazex.algo.MazeGenerator.Companion.DY
import mth.jmazex.algo.MazeGenerator.Companion.opposite
import mth.jmazex.algo.RecursiveBacktracking.Neighbor
import mth.jmazex.app.Maze
import mth.jmazex.app.Point
import java.lang.System.currentTimeMillis
import javax.swing.SwingConstants.*

fun main() {
    val mg = HuntAndKill()
    val maze = Maze(0, 0)
    val times = ArrayList<Double>(50)
    val repeatedMeasures = ArrayList<Long>(21)

    (5..50).forEach { dim ->
        for (i in 1..20) {
            maze.rebuild(dim, dim, true)
            var time = currentTimeMillis()
            mg.generate(0, 0, dim, dim, maze)
            time = currentTimeMillis() - time
            repeatedMeasures.add(time)
        }

        times.add(repeatedMeasures.average())
        repeatedMeasures.clear()
    }

    (5..50).forEachIndexed { index, dim -> println("$dim -> ${times[index]}") }
}

class HuntAndKill : MazeGenerator {

    private val VISITED = "visited"
    private var visitedCounter = 0
    private lateinit var maze: Maze
    private var width = 0
    private var height = 0

    private var xStart = 0
    private var yStart = 0

    fun isSelectionGeneration(xStart: Int, yStart: Int, width: Int, height: Int, maze: Maze) =
        !(xStart == 0 && yStart == 0 && width == maze.nx && height == maze.ny)


    override fun generate(xStart: Int, yStart: Int, width: Int, height: Int, maze: Maze): Maze {
        // mark all cells as not visited
        for (x in 0 until width)
            for (y in 0 until height) {
                maze.cellAt(x + xStart, y + yStart)[VISITED] = false

                if (isSelectionGeneration(xStart, yStart, width, height, maze))
                    rebuildWalls(maze, x, y, Point(xStart, yStart), Point(width, height))
            }

        this.visitedCounter = 0
        this.width = width
        this.height = height
        this.maze = maze
        this.xStart = xStart
        this.yStart = yStart

        var walkStart = Point(xStart + random.nextInt(width), yStart + random.nextInt(height))

        while (visitedCounter < width * height) {
            randomWalk(walkStart.first, walkStart.second)
            val neighbor = hunting()

            if (neighbor == null) {
                break
            } else
                walkStart = Point(neighbor.first, neighbor.second)
        }

        return maze
    }

    private fun hunting(): Point? {
        for (x in 0 until width)
            for (y in 0 until height) {
                if (maze.cellAt(x + xStart, y + yStart)[VISITED] == false) {
                    // search for visited neighborhoods
                    val directions = getAvailableDirections(x + xStart, y + yStart, false)

                    if (directions.isNotEmpty()) {
                        carvePassage(x + xStart, y + yStart, directions[0])
                        return Point(x + xStart, y + yStart)
                    }
                }
            }

        return null
    }

    /**
     * Opens a passage from the cell (x,y) to the cell specified by [direction]
     */
    private fun carvePassage(x: Int, y: Int, direction: Int): Point {
        val destinationX = x + DX[direction]!!
        val destinationY = y + DY[direction]!!

        maze.cellAt(x, y).walls[direction] = false
        maze.cellAt(destinationX, destinationY).walls[opposite.getValue(direction)] = false

        return Point(destinationX, destinationY)
    }

    /**
     * Search for the available moves around the cell identified by the absolute coordinates [x] and [y]
     * @param x The absolute horizontal coordinate of the cell
     * @param y The absolute vertical coordinate of the cell
     * @param searchForNeighborsUnvisited A flag indicating the search goal
     */
    private fun getAvailableDirections(
        x: Int,
        y: Int,
        searchForNeighborsUnvisited: Boolean = true
    ): List<Int> {
        val directionsAllowed = mutableSetOf(NORTH, SOUTH, EAST, WEST)
        val directions = mutableListOf<Int>()

        if (x - xStart == 0)
            directionsAllowed.remove(WEST)
        if (y - yStart == 0)
            directionsAllowed.remove(NORTH)
        if (x - xStart == width - 1)
            directionsAllowed.remove(EAST)
        if (y - yStart == height - 1)
            directionsAllowed.remove(SOUTH)

        directionsAllowed.forEach {
            val visited = maze.cellAt(x + DX[it]!!, y + DY[it]!!)[VISITED] == true

            if (!visited == searchForNeighborsUnvisited)
                directions.add(it)
        }

        return directions
    }


    /**
     * Make a random walk, beginning from the absolute coordinates [fromX] e [fromY] until it reach a
     * dead end
     */
    private fun randomWalk(fromX: Int, fromY: Int) {
        var currentX = fromX
        var currentY = fromY
        var directions = getAvailableDirections(fromX, fromY)
        maze.cellAt(fromX, fromY)[VISITED] = true
        visitedCounter++

        while (directions.isNotEmpty()) {
            val direction = directions[random.nextInt(directions.size)]
            val (x, y) = carvePassage(currentX, currentY, direction)

            maze.cellAt(x, y)[VISITED] = true
            visitedCounter++

            currentX = x
            currentY = y

            directions = getAvailableDirections(x, y)
        }
    }
}