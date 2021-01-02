package mth.maze

class Maze(nx: Int = 0, ny: Int = 0, allWalls: Boolean = false) {

    /**
     * The width of the maze, i.e. the cell number in the horizontal direction
     */
    val width get() = grid.size

    /**
     * The height of the maze, i.e. the cell number in the vertical direction
     */
    val height get() = if (width == 0) 0 else grid[0].size

    var grid: Array<Array<Cell>> = Array(nx) {
        Array(ny) { Cell(wallMap(allWalls)) }
    }

    fun rebuild(nx: Int, ny: Int, allWalls: Boolean): Maze {
        grid = Array(nx) {
            Array(ny) { Cell(wallMap(allWalls)) }
        }

        return this
    }

    fun cellAt(x: Int, y: Int) = grid[x][y]

    class Cell(var walls: BooleanArray = BooleanArray(4)) {
        var properties = mutableMapOf<String, Any>()

        operator fun set(key: String, value: Any) {
            properties[key] = value
        }

        operator fun get(key: String) = properties[key]
    }

    companion object {

        const val NORTH = 0
        const val SOUTH = 1
        const val EAST = 2
        const val WEST = 3

        fun wallMap(built: Boolean) = BooleanArray(4) { built }
    }
}