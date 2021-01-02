package mth.jmazex.algo

import mth.jmazex.algo.MazeGenerator.Companion.DX
import mth.jmazex.algo.MazeGenerator.Companion.DY
import mth.jmazex.app.Maze
import mth.jmazex.app.Point
import java.util.*
import javax.swing.SwingConstants.*


object PathFinder {

    const val visitedProp = "s_visited"
    lateinit var maze: Maze
    val rnd = Random()
    val cellTotal get() = maze.nx * maze.ny
    var visitedCount = 0
    var found = false
    val track: Stack<Point> = Stack()
    var init = false

    fun init(maze: Maze, start: Point) {
        this.maze = maze
        track.clear()
        track.push(start)

        init = true
    }

    fun next(end: Point) {
        if (track.isNotEmpty() && visitedCount < cellTotal) {
            val current = track.peek()
            val (x, y) = current
            maze.cellAt(x, y)[visitedProp] = true
            println(current)

            if (current == end) {
                // we have reach the end position
                found = true
                println("FOUND ($x, $y)")
                return
            }

            val neighbors = getAvailableNeighbors(x, y, maze)
            println(neighbors)

            if (neighbors.isEmpty())
                track.pop()
            else {
                val ng = neighbors[rnd.nextInt(neighbors.size)]
                println(ng)
                track.push(ng)
                visitedCount++
            }
        }
    }

    fun findPath(start: Point, end: Point, maze: Maze): Stack<Point> {
        val cellTotal = maze.nx * maze.ny
        var visitedCount = 0
        var found = false
        val track: Stack<Point> = Stack()
        track.push(start)

        while (track.isNotEmpty() && visitedCount < cellTotal) {
            val current = track.peek()
            val (x, y) = current
            maze.cellAt(x, y)[visitedProp] = true
            println(current)

            if (current == end) {
                // we have reach the end position
                found = true
                break
            }

            val neighbors = getAvailableNeighbors(x, y, maze)

            if (neighbors.isEmpty())
                track.pop()
            else {
                val ng = neighbors[rnd.nextInt(neighbors.size)]
                track.push(ng)
                visitedCount++
            }
        }

        if (!found) {
            System.err.println("NOT FOUND")
            track.clear()
        }

        // removes the visited flag from the cells
        for (i in 0 until maze.nx)
            for (j in 0 until maze.ny)
                maze.cellAt(i, j).properties.remove(visitedProp)

        return track
    }

    private fun getAvailableNeighbors(
        x: Int,
        y: Int,
        maze: Maze
    ): MutableList<Point> {
        val neighbors = mutableListOf<Point>()

        arrayOf(NORTH, SOUTH, EAST, WEST).forEach { direction ->
            if (maze.cellAt(x, y).walls.getValue(direction).not()) {
                //there's no wall
                val visited = maze.cellAt(x + DX[direction]!!, y + DY[direction]!!).properties.getOrDefault(
                    visitedProp,
                    false
                ) as Boolean

                if (!visited)
                    neighbors.add(Point(x + DX[direction]!!, y + DY[direction]!!))
            }

        }

        return neighbors
    }
}