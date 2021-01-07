package mth.maze

import mth.maze.algo.HuntAndKill
import mth.maze.algo.RecursiveDivision
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*

operator fun Point.component1(): Int = this.x
operator fun Point.component2(): Int = this.y

fun Maze.show(cellSize: Int = 18, action: () -> Unit = {}) {
    val maze: Maze = this

    JFrame("Maze preview").apply {
        val drawArea = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                val g2d = g as Graphics2D
                val xOffset = (this.size.width - maze.width * cellSize) / 2
                val yOffset = 30

                minimumSize = Dimension(size.width, yOffset * 2 + cellSize * maze.height)
                preferredSize = minimumSize

                g2d.clearRect(0, 0, width, height)

                // draw horizontal cell numbers
                for (x in 0 until maze.width)
                    g2d.drawString("$x", xOffset + x * cellSize, yOffset - 8)

                // draw vertical cell numbers
                for (y in 0 until maze.height)
                    g2d.drawString("$y", xOffset - 20, yOffset + y * cellSize + 10)

                g2d.stroke = BasicStroke(3.0f)

                for (i in 0 until maze.width)
                    for (j in 0 until maze.height) {
                        val x = xOffset + i * cellSize
                        val y = yOffset + j * cellSize

                        with(maze.cellAt(i, j)) {
                            if (walls[Maze.NORTH])
                                g2d.drawLine(x + 0, y + 0, x + cellSize, y + 0)
                            if (walls[Maze.SOUTH])
                                g2d.drawLine(x + 0, y + cellSize, x + cellSize, y + cellSize)
                            if (walls[Maze.WEST])
                                g2d.drawLine(x + 0, y + 0, x + 0, y + cellSize)
                            if (walls[Maze.EAST])
                                g2d.drawLine(
                                    x + cellSize, y + 0, x + cellSize, y + cellSize
                                )
                        }
                    }
            }
        }

        val bottomPane = JPanel(FlowLayout()).apply {
            add(JButton(object : AbstractAction("Run action") {
                override fun actionPerformed(e: ActionEvent?) {
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

fun main() {
    val w = 40
    val h = 40
    val maze = Maze(w, h, true)
    val algo = RecursiveDivision()
//    algo.init(MazeRegion(0, 0, w, h), maze)
//    algo.generate(MazeRegion(2,2, w-2,2), maze)
//    Kruskal().generate(MazeRegion(2, 5, 16, 10), maze)
    HuntAndKill().generate(MazeRegion(0, 0, w, h), maze)
//    algo.init(maze)

    maze.show(cellSize = 10, action = { algo.next() })
}
