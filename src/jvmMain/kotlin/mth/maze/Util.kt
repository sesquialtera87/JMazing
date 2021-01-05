package mth.maze

import java.awt.*
import java.awt.BorderLayout
import javax.swing.*
import mth.maze.algo.*

operator fun Point.component1(): Int = this.x

operator fun Point.component2(): Int = this.y

fun Maze.show(cellSize: Int = 18, action: () -> Unit = {}) {
    val maze: Maze = this

    JFrame("Maze preview").apply {
        val drawArea =
                object : JPanel() {
                    override fun paintComponent(g: Graphics) {
                        val g2d = g as Graphics2D
                        val xOffset = (this.size.width - maze.width * cellSize) / 2
                        val yOffset = 30

                        minimumSize = Dimension(size.width, yOffset * 2 + cellSize * maze.height)
                        preferredSize = minimumSize

                        g2d.clearRect(0, 0, width, height)

                        // draw horizontal cell numbers
                        for (x in 0 until maze.width) g2d.drawString(
                                "$x", xOffset + x * cellSize, yOffset - 8)

                        // draw vertical cell numbers
                        for (y in 0 until maze.height) g2d.drawString(
                                "$y", xOffset - 20, yOffset + y * cellSize + 10)

                        g2d.stroke = BasicStroke(3.0f)

                        for (i in 0 until maze.width) for (j in 0 until maze.height) {
                            val x = xOffset + i * cellSize
                            val y = yOffset + j * cellSize

                            with(maze.cellAt(i, j)) {
                                if (walls[Maze.NORTH])
                                        g2d.drawLine(x + 0, y + 0, x + cellSize, y + 0)
                                if (walls[Maze.SOUTH])
                                        g2d.drawLine(
                                                x + 0, y + cellSize, x + cellSize, y + cellSize)
                                if (walls[Maze.WEST])
                                        g2d.drawLine(x + 0, y + 0, x + 0, y + cellSize)
                                if (walls[Maze.EAST])
                                        g2d.drawLine(
                                                x + cellSize, y + 0, x + cellSize, y + cellSize)
                            }
                        }
                    }
                }

        val bottomPane =
                JPanel(FlowLayout()).apply {
                    add(
                            JButton(
                                    object : AbstractAction("Run action") {
                                        override fun actionPerformed(
                                                e: java.awt.event.ActionEvent
                                        ) {
                                            action.invoke()
                                            drawArea.repaint()
                                        }
                                    }))
                }

        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.preferredSize = Dimension(600, 400)
        this.layout = BorderLayout()
        this.add(JScrollPane(drawArea), BorderLayout.CENTER)
        this.add(bottomPane, BorderLayout.SOUTH)
        this.pack()
        isVisible = true
    }
}

fun Maze.setCellAt(x: Int, y: Int, cell: Maze.Cell) {
    grid[x][y] = cell
}

fun Maze.swapCells(x1: Int, y1: Int, x2: Int, y2: Int) {
    val c = grid[x1][y1]
    grid[x1][y1] = grid[x2][y2]
    grid[x2][y2] = c
}

/**
 * Transform the maze through a reflection along an axis (vertical or horizontal) passing through
 * the center of the maze
 */
fun Maze.reflect(horizontalAxis: Boolean = true) {
    fun swapWalls(cell: Maze.Cell, w1: Int, w2: Int) {
        val wall = cell.walls[w1]
        cell.walls[w1] = cell.walls[w2]
        cell.walls[w2] = wall
    }

    if (horizontalAxis) {
        var y1: Int
        var count = height / 2 - 1

        for (ix in 0 until width) for (iy in 0..count) {
            y1 = height - 1 - iy

            swapCells(ix, iy, ix, y1)

            // swap north and south walls
            swapWalls(cellAt(ix, iy), Maze.NORTH, Maze.SOUTH)
            swapWalls(cellAt(ix, y1), Maze.NORTH, Maze.SOUTH)
        }
    } else {
        var x1: Int
        var count = width / 2 - 1

        for (iy in 0 until height) for (ix in 0..count) {
            x1 = width - 1 - ix

            swapCells(ix, iy, x1, iy)

            // swap north and south walls
            swapWalls(cellAt(ix, iy), Maze.EAST, Maze.WEST)
            swapWalls(cellAt(x1, iy), Maze.EAST, Maze.WEST)
        }
    }
}

fun main() {
    val w = 50
    val h = 50
    val maze = Maze(w, h, true)
    HuntAndKill().generate(MazeRegion(0, 0, w, h), maze)
    // maze.rebuild(3, 2, false)
    // maze.cellAt(0, 0).walls = booleanArrayOf(true, true, false, true)
    // maze.cellAt(0, 1).walls = booleanArrayOf(true, false, true, false)
    maze.show(10, action = { maze.reflect(false) })
}
