package mth.maze

/**
 * Construct a maze-grid of size `nx*ny`.
 * @param nx The maze width
 * @param ny The maze height
 * @param allWalls If `true`, the grid is initialized with all walls built
 */
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

    /**
     * Construct a new grid of size `nx*ny`
     * @param allWalls If `true`, the grid is initialized with all walls built
     */
    fun rebuild(nx: Int, ny: Int, allWalls: Boolean): Maze {
        grid = Array(nx) {
            Array(ny) { Cell(wallMap(allWalls)) }
        }

        return this
    }

    /**
     * Returns the [mth.maze.Maze.Cell] object located in the specified coordinates (x,y)
     */
    fun cellAt(x: Int, y: Int) = grid[x][y]

    /**
     * A property container of a maze cell. It consists of a [MutableMap] with [String]-[Any]
     * pairing couples.
     * @param walls A [BooleanArray] containing information on the cell walls. The value for a particular
     * direction can be obtained using the constants [NORTH], [SOUTH], [EAST], [WEST]. For example, with
     * `walls[NORTH]` one can get the information on the north-side wall
     */
    class Cell(var walls: BooleanArray = BooleanArray(4)) {
        private var properties = mutableMapOf<String, Any>()

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

        fun wallMap(walls_built: Boolean) = BooleanArray(4) { walls_built }
    }
}