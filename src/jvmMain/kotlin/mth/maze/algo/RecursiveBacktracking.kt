package mth.jmazex.algo


//class RecursiveBacktracking(var startCell: Point = Pair(-1, -1)) : MazeGenerator {
//
//    private lateinit var grid: Array<Array<Cell>>
//    private val rn = Random()
//    private val opposite: Map<Int, Int> = mapOf(NORTH to SOUTH, SOUTH to NORTH, WEST to EAST, EAST to WEST)
//
//    object Test {
//        lateinit var canvas: javafx.scene.canvas.Canvas
//        private val opposite = mapOf(NORTH to SOUTH, SOUTH to NORTH, WEST to EAST, EAST to WEST)
//        val DX = mapOf(NORTH to 0, SOUTH to 0, EAST to +1, WEST to -1)
//        val DY = mapOf(NORTH to -1, SOUTH to +1, EAST to 0, WEST to 0)
//        var x = 0
//        var y = 0
//        val track = Stack<Pair<Int, Int>>()
//        var grid: Array<Array<Cell>>
//
//        init {
//            track.push(Pair(x, y))
//            grid = Array(16) {
//                Array(16) {
//                    Cell(
//                        mutableMapOf(
//                            NORTH to true,
//                            SOUTH to true,
//                            EAST to true,
//                            WEST to true
//                        )
//                    )
//                }
//            }
//        }
//
//        private fun getAvailableNeighbors(x: Int, y: Int, nx: Int, ny: Int): List<Neighbor> {
//            val directionsAllowed = mutableSetOf(NORTH, SOUTH, EAST, WEST)
//            val neighbors = mutableListOf<Neighbor>()
//
//            if (x == 0)
//                directionsAllowed.remove(WEST)
//            if (y == 0)
//                directionsAllowed.remove(NORTH)
//            if (x == nx - 1)
//                directionsAllowed.remove(EAST)
//            if (y == ny - 1)
//                directionsAllowed.remove(SOUTH)
//
//            directionsAllowed.forEach {
//                val visited = grid[x + DX[it]!!][y + DY[it]!!].properties.getOrDefault("visited", false) as Boolean
//
//                if (!visited)
//                    neighbors.add(Neighbor(x + DX[it]!!, y + DY[it]!!, it))
//            }
//
//            return neighbors
//        }
//
//        private fun carvePassage(neighborhood: Neighbor) {
//            val (x, y, direction) = neighborhood
//
//            val oppositeDirection = opposite.getValue(direction)
//            grid[x][y].walls[oppositeDirection] = false
//            grid[x - DX[direction]!!][y - DY[direction]!!].walls[direction] = false
//
//            println(grid[x][y].walls)
//            println(grid[x - DX[direction]!!][y - DY[direction]!!].walls)
////            when (direction) {
////                NORTH -> {
////                    grid[x][y].walls[NORTH] = false
////                    grid[x][y - 1].walls[SOUTH] = false
////                }
////                SOUTH -> {
////                    grid[x][y].walls[SOUTH] = false
////                    grid[x][y + 1].walls[NORTH] = false
////                }
////                WEST -> {
////                    grid[x][y].walls[WEST] = false
////                    grid[x - 1][y].walls[EAST] = false
////                }
////                EAST -> {
////                    grid[x][y].walls[EAST] = false
////                    grid[x + 1][y].walls[WEST] = false
////                }
////            }
//        }
//
//        fun draw(x: Int, y: Int) {
//            val gc = canvas.graphicsContext2D
//
//            gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
//            gc.fill = Color.RED
//            gc.translate(MainView.xOffset, MainView.yOffset)
//            gc.fillRect(
//                3 + x * MainView.cellWidth,
//                3 + y * MainView.cellHeight,
//                MainView.cellWidth - 6,
//                MainView.cellHeight - 6
//            )
//            gc.translate(-MainView.xOffset, -MainView.yOffset)
//
//            gc.translate(MainView.xOffset, MainView.yOffset)
//
//
//            gc.lineWidth = MainView.wallLineWidth
//            gc.stroke = MainView.wallColor
//
//            for (i in 0 until 16)
//                for (j in 0 until 16) {
//                    gc.translate(i * MainView.cellWidth, j * MainView.cellHeight)
//                    val wallMap = grid[i][j].walls
//
//                    if (wallMap.getValue(NORTH))
//                        gc.strokeLine(0.0, 0.0, MainView.cellWidth, 0.0)
//                    if (wallMap.getValue(SOUTH))
//                        gc.strokeLine(
//                            0.0,
//                            MainView.cellHeight,
//                            MainView.cellWidth,
//                            MainView.cellHeight
//                        )
//                    if (wallMap.getValue(EAST))
//                        gc.strokeLine(
//                            MainView.cellWidth, 0.0,
//                            MainView.cellWidth,
//                            MainView.cellHeight
//                        )
//                    if (wallMap.getValue(WEST))
//                        gc.strokeLine(0.0, 0.0, 0.0, MainView.cellHeight)
//
//                    gc.translate(-i * MainView.cellWidth, -j * MainView.cellHeight)
//                }
//
//
//            gc.translate(-MainView.xOffset, -MainView.yOffset)
//        }
//
//
//        fun next() {
//            if (track.isNotEmpty()) {
//                val (currentX, currentY) = track.peek()
//                draw(currentX, currentY)
//                println("Current = ${track.peek()}")
//                grid[currentX][currentY]["visited"] = true
//                val neighbors: List<Neighbor> = getAvailableNeighbors(currentX, currentY, 16, 16)
//                println(neighbors)
//
//                if (neighbors.isEmpty())
//                    track.pop()
//                else {
//                    val ng = neighbors[Random().nextInt(neighbors.size)]
//                    println("Random neig = $ng")
//                    track.push(Pair(ng.x, ng.y))
//                    carvePassage(ng)
//                }
//            }
//        }
//    }
//
//    override fun generate(nx: Int, ny: Int, width: Int, height: Int, maze: Maze): Maze {
//        var (x, y) = startCell
//
//        // if the initial cell isn't specified or lives out of the boundaries, choose a cell randomly
//        if (x < 0 || y < 0 || x >= width || y >= height) {
//            x = rn.nextInt(nx)
//            y = rn.nextInt(ny)
//            println("Start recursive backtracking from ($x, $y)")
//        }
//
//        var cellVisitedCount = 1
//        val cellTotal = nx * ny
//        val track = Stack<Point>()
//        track.push(Point(x, y))
//
//        grid = Array(nx) {
//            Array(ny) {
//                Cell(
//                    mutableMapOf(
//                        NORTH to true,
//                        SOUTH to true,
//                        EAST to true,
//                        WEST to true
//                    )
//                )
//            }
//        }
//
//        while (track.isNotEmpty() && cellVisitedCount < cellTotal) {
//            val (currentX, currentY) = track.peek()
//            grid[currentX][currentY]["visited"] = true
//
//            val neighbors: List<Neighbor> = getAvailableNeighbors(currentX, currentY, nx, ny)
//
//            if (neighbors.isEmpty())
//                track.pop()
//            else {
//                val ng = neighbors[rn.nextInt(neighbors.size)]
//                track.push(Pair(ng.x, ng.y))
//                carvePassage(ng)
//                cellVisitedCount++
//            }
//        }
//
//        val maze = Maze(nx, ny)
//        maze.grid = grid
//
//        return maze
//    }
//
//    private fun getAvailableNeighbors(x: Int, y: Int, nx: Int, ny: Int): List<Neighbor> {
//        val directionsAllowed = mutableSetOf(NORTH, SOUTH, EAST, WEST)
//        val neighbors = mutableListOf<Neighbor>()
//
//        if (x == 0)
//            directionsAllowed.remove(WEST)
//        if (y == 0)
//            directionsAllowed.remove(NORTH)
//        if (x == nx - 1)
//            directionsAllowed.remove(EAST)
//        if (y == ny - 1)
//            directionsAllowed.remove(SOUTH)
//
//        directionsAllowed.forEach {
//            val visited = grid[x + DX[it]!!][y + DY[it]!!].properties.getOrDefault(
//                "visited",
//                false
//            ) as Boolean
//
//            if (!visited)
//                neighbors.add(Neighbor(x + DX[it]!!, y + DY[it]!!, it))
//        }
//
//        return neighbors
//    }
//
//    private fun carvePassage(neighborhood: Neighbor) {
//        val (x, y, direction) = neighborhood
//
//        grid[x][y].walls[opposite.getValue(direction)] = false
//        grid[x - DX[direction]!!][y - DY[direction]!!].walls[direction] = false
//    }
//
//    data class Neighbor(var x: Int, var y: Int, var direction: Int)
//}