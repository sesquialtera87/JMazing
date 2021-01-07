package mth.maze.algo

import mth.maze.Maze
import mth.maze.MazeRegion
import java.util.*

/**
 * Create a maze using the recursive division algorithm
 * (see for example [Maze Generation: Recursive Division](http://weblog.jamisbuck.org/2011/1/12/maze-generation-recursive-division-algorithm))
 */
class RecursiveDivision : MazeGenerator {

    override fun generate(mazeRegion: MazeRegion, maze: Maze): Maze {
        val stack = Stack<MazeRegion>()
        stack.push(mazeRegion)

        while (stack.isNotEmpty()) {
            val region = stack.pop()

            when (regionShape(region)) {
                PORTRAIT -> splitHorizontally(region, maze).forEach { stack.push(it) }
                LANDSCAPE -> splitVertically(region, maze).forEach { stack.push(it) }
                SQUARE -> {
                    if (random.nextInt(2) == 0)
                        splitVertically(region, maze).forEach { stack.push(it) }
                    else splitHorizontally(region, maze).forEach { stack.push(it) }
                }
            }
        }

        return maze
    }

    private val stack = Stack<MazeRegion>()
    var maze: Maze = Maze()

    fun init(maze: Maze) {
        this.maze = maze
        stack.clear()
        stack.push(MazeRegion(0, 0, maze.width, maze.height))
    }

    fun next() {
        if (stack.isNotEmpty()) {
            val region = stack.pop()

            when (regionShape(region)) {
                PORTRAIT -> splitHorizontally(region, maze)
                LANDSCAPE -> splitVertically(region, maze)
                SQUARE -> {
                    if (random.nextInt(2) == 0)
                        splitVertically(region, maze)
                    else splitHorizontally(region, maze)
                }
            }
        } else
            println("No region to subdivide")
    }

    private fun splitVertically(region: MazeRegion, maze: Maze): Array<MazeRegion> {
        with(region) {
            println("Splitting region $region")

            // is a vertical 1-cell strip
            if (height < 2) {
                println("ignoring")
                return emptyArray()
            }

            val divisionLocation = random.nextInt(width - 1) + 1
            val doorLocation = random.nextInt(height)

            println("Vertical split at x=$divisionLocation")
            println("Door at y=$doorLocation")

            buildWall(false, doorLocation, divisionLocation, region, maze)

            val r1 = MazeRegion(region.x, region.y, divisionLocation, region.height)
            stack.push(r1)
            val r2 = MazeRegion(divisionLocation + region.x, region.y, region.width - divisionLocation, region.height)
            stack.push(r2)

            println(r1)
            println(r2)

            return arrayOf(r1, r2)
        }
    }

    private fun splitHorizontally(region: MazeRegion, maze: Maze): Array<MazeRegion> {
        println("Splitting region $region")

        with(region) {
            // is a vertical 1-cell strip
            if (width < 2) {
                println("ignoring")
                return emptyArray()
            }

            val divisionLocation = random.nextInt(height - 1) + 1
            val doorLocation = random.nextInt(width)

            println("Horizontal split at y=$divisionLocation")
            println("Door at x=$doorLocation")

            buildWall(true, doorLocation, divisionLocation, region, maze)

            val r1 = MazeRegion(region.x, region.y, region.width, divisionLocation)
            val r2 = MazeRegion(region.x, region.y + divisionLocation, region.width, region.height - divisionLocation)
            println(r1)
            println(r2)

            stack.push(r1)
            stack.push(r2)

            return arrayOf(r1, r2)
        }
    }

    /**
     * Build a wall between the cells (x, divisionLocation-1) and (x,
     * divisionLocation) with x in []
     *
     * @param horizontal
     * @param doorLocation
     * @param divisionLocation
     * @param maze
     * @param region
     */
    private fun buildWall(
        horizontal: Boolean, doorLocation: Int, divisionLocation: Int, region: MazeRegion,
        maze: Maze
    ) {
        if (horizontal) {
            for (x in 0 until region.width) {
                if (x == doorLocation)
                    continue

                maze.cellAt(x + region.x, region.y + divisionLocation).walls[Maze.NORTH] = true // north wall
                maze.cellAt(x + region.x, region.y + divisionLocation - 1).walls[Maze.SOUTH] = true
            }
        } else {
            for (y in 0 until region.height) {
                if (y == doorLocation)
                    continue

                maze.cellAt(region.x + divisionLocation - 1, y + region.y).walls[Maze.EAST] = true
                maze.cellAt(region.x + divisionLocation, y + region.y).walls[Maze.WEST] = true
            }
        }
    }

    /**
     * Returns the shape the region has.
     * @param region The region of the maze to valuate
     * @return The region shape, a value of [LANDSCAPE],[PORTRAIT] or [SQUARE]
     */
    private fun regionShape(region: MazeRegion): Int {
        with(region) {
            return when {
                width == height -> SQUARE
                width < height -> PORTRAIT
                else -> LANDSCAPE
            }
        }
    }

    companion object {
        private const val LANDSCAPE = 0
        private const val PORTRAIT = 1
        private const val SQUARE = -1
    }
}