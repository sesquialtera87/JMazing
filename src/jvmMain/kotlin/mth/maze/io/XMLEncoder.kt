package mth.maze.io

import mth.maze.Maze
import mth.maze.Maze.Companion.EAST
import mth.maze.Maze.Companion.NORTH
import mth.maze.Maze.Companion.SOUTH
import mth.maze.Maze.Companion.WEST
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*


class XMLEncoder {

    private val directions = arrayOf(NORTH, SOUTH, EAST, WEST)
    private val directionToAttribute = mapOf(NORTH to "N", SOUTH to "S", EAST to "E", WEST to "W")

    fun encode(maze: Maze, outputPath: Path) {
        val date = SimpleDateFormat("YYYY-MM-DD").format(Date(System.currentTimeMillis()))
        val time = SimpleDateFormat("hh:mm:ss").format(Date(System.currentTimeMillis()))
        val writer = FileWriter(outputPath.toFile(), StandardCharsets.UTF_8)

        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        writer.append("<maze width=\"${maze.width}\" height=\"${maze.height}\" timestamp=\"${date}T$time\"> \n")

        for (x in 0 until maze.width) {
            writer.append("<row id=\"$x\"> \n")

            for (y in 0 until maze.height) {
                writer.append("<cell x=\"$x\" y=\"$y\" ${wallAttributes(x, y, maze)}/>\n")
            }

            writer.append("</row>\n")
        }

        writer.append("</maze>")
        writer.close()
    }

    private fun wallAttributes(x: Int, y: Int, maze: Maze): String {
        val b = StringBuilder()

        directions.forEach {
            b.append(directionToAttribute[it]).append("=\"").append(maze.cellAt(x, y).walls[it]).append("\" ")
        }
        return b.toString()
    }
}