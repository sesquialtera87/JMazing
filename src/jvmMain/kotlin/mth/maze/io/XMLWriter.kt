package mth.maze.io

import mth.maze.Maze
import mth.maze.algo.MazeGenerator
import mth.maze.algo.Prim
import java.io.FileOutputStream
import javax.xml.stream.XMLOutputFactory

class XMLWriter {
    fun write(maze: Maze, path: String) {
        val factory = XMLOutputFactory.newInstance()
        val writer = factory.createXMLStreamWriter(FileOutputStream(path))

        writer.apply {
            writeStartDocument("UTF-8", "1.0")
            writeStartElement("maze")
            writeAttribute("version", "1.0")
            writeAttribute("width", "${maze.width}")
            writeAttribute("height", "${maze.height}")
        }

        for (row in 0 until maze.width)
            for (col in 0 until maze.height) {
                val cell = maze.cellAt(row, col)

                with(writer) {
                    writeEmptyElement("cell")

                    writeAttribute("x", "$row")
                    writeAttribute("y", "$col")
                    writeAttribute("north", "${cell.walls[Maze.NORTH]}")
                    writeAttribute("south", "${cell.walls[Maze.SOUTH]}")
                    writeAttribute("east", "${cell.walls[Maze.EAST]}")
                    writeAttribute("west", "${cell.walls[Maze.WEST]}")
                }
            }

        writer.writeEndDocument()
        writer.close()
    }
}

fun main() {
    val path = "/home/mattia/IdeaProjects/JMazing/src/jvmMain/kotlin/mth/maze/io/maze_test.xml"
    var maze = Maze(10, 10, true)
    val algo: MazeGenerator = Prim()
    maze = algo.generate(maze.getRegion(), maze)
//    PreviewWindow(maze).showMaze()
    XMLWriter().write(maze, path)
}
