package mth.maze

import mth.maze.algo.Prim
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*

operator fun Point.component1(): Int = this.x
operator fun Point.component2(): Int = this.y

class PreviewWindow(
    maze: Maze,
    action: () -> Unit = {},
    actionCaption: String = "Run action",
    cellSize: Int = 12,
    windowWidth: Int = 600,
    windowHeight: Int = 600,
    wallStroke: Float = 3.0f,
    cellNumberFontSize: Float = 9.0f
) :
    JFrame() {

    val yOffset = 30

    private val bottomPane = JPanel(FlowLayout()).apply {
        add(JButton(object : AbstractAction(actionCaption) {
            override fun actionPerformed(e: ActionEvent?) {
                action.invoke()
                drawArea.repaint()
            }
        }))
    }

    val drawArea = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            val g2d = g as Graphics2D
            val xOffset = (this.size.width - maze.width * cellSize) / 2
            val transform = g2d.transform

            minimumSize = Dimension(size.width, yOffset * 2 + cellSize * maze.height)
            preferredSize = minimumSize

            g2d.clearRect(0, 0, width, height)

            // set the font dimension for cell numbers
            g2d.font = g2d.font.deriveFont(cellNumberFontSize)

            // draw horizontal cell numbers
            g2d.translate(xOffset, 0)

            for (x in 0 until maze.width) {
                g2d.rotate(0.0)
                g2d.drawString("$x", 0, yOffset - 7)
                g2d.rotate(0.0)
                g2d.translate(cellSize, 0)
//                g2d.drawString("$x", xOffset + x * cellSize, yOffset - 8)
            }

            // restore orientation
            g2d.transform = transform

            // draw vertical cell numbers
            for (y in 0 until maze.height)
                g2d.drawString("$y", xOffset - 20, yOffset + y * cellSize + 10)

            // set the stroke width for the walls
            g2d.stroke = BasicStroke(wallStroke)

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

    init {
        this.title = "Maze preview"
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.preferredSize = Dimension(windowWidth, windowHeight)
        this.layout = BorderLayout()
        this.add(JScrollPane(drawArea), BorderLayout.CENTER)
        this.add(bottomPane, BorderLayout.SOUTH)
        this.pack()
    }

    fun showMaze() {
        this.isVisible = true
    }
}

fun Maze.show(cellSize: Int = 18, action: () -> Unit = {}) {
    PreviewWindow(this, cellSize = cellSize, action = action, wallStroke = 2.5f).showMaze()
}

fun main() {
    val w = 40
    val h = 40
    val maze = Maze(w, h, true)
    val algo = Prim()
//    algo.init(MazeRegion(0, 0, w, h), maze)
//    algo.generate(MazeRegion(2,2, w-2,2), maze)
//    Kruskal().generate(MazeRegion(2, 5, 16, 10), maze)
    algo.generate(MazeRegion(0, 0, w, h), maze)
//    algo.init(maze)

    maze.show(cellSize = 10, action = { })
}
