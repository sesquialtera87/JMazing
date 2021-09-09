package mth.maze.io

import com.json_simple.JsonObject
import mth.maze.Maze
import mth.maze.algo.MazeGenerator
import mth.maze.algo.Prim
import mth.maze.show
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.xml.XMLConstants
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent
import javax.xml.transform.stax.StAXSource
import javax.xml.validation.SchemaFactory

const val ATTR_WIDTH = "width"
const val ATTR_HEIGHT = "height"

class XMLWriter {
    fun write(maze: Maze, path: String, xmlVersion: String = "1.0") {
        val factory = XMLOutputFactory.newInstance()
        val writer = factory.createXMLStreamWriter(FileOutputStream(path))


        writer.apply {
            writeStartDocument("UTF-8", "1.0")
            writeStartElement(TAG_MAZE)
            writeAttribute("version", xmlVersion)
            writeAttribute(ATTR_WIDTH, "${maze.width}")
            writeAttribute(ATTR_HEIGHT, "${maze.height}")
        }

        for (row in 0 until maze.width)
            for (col in 0 until maze.height) {
                val cell = maze.cellAt(row, col)

                with(writer) {
                    writeEmptyElement(TAG_CELL)

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

const val TAG_MAZE = "maze"
const val TAG_CELL = "cell"
const val XML_SCHEMA = "maze_xml_schema.xsd"
const val path = "/home/mattia/IdeaProjects/JMazing/src/jvmMain/kotlin/mth/maze/io/maze_test.xml"

fun validateXML(xmlStreamReader: XMLStreamReader? = null) {
    val reader = xmlStreamReader ?: XMLInputFactory.newInstance().createXMLStreamReader(FileInputStream(path))

    val schema = SchemaFactory
        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .newSchema(XMLWriter::class.java.getResource(XML_SCHEMA))
    schema.newValidator().validate(StAXSource(reader))
}

fun readFromXML(path: String, errorHandler: ErrorHandler? = null): Maze {
    val maze = Maze()
    var mazeWidth = 0
    var mazeHeight = 0

    // cell coordinates
    var x: Int
    var y: Int

    val wallMap = mapOf("north" to Maze.NORTH, "west" to Maze.WEST, "east" to Maze.EAST, "south" to Maze.SOUTH)

    val reader = XMLInputFactory.newInstance().createXMLStreamReader(FileInputStream(path))

    while (reader.hasNext()) {
        when (reader.next()) {
            XMLEvent.START_ELEMENT -> {
                when (reader.localName) {
                    TAG_MAZE -> {
                        // read the maze dimension from the attributes and build the grid accordingly
                        mazeWidth = reader.getAttributeValue(null, ATTR_WIDTH).toInt()
                        mazeHeight = reader.getAttributeValue(null, ATTR_HEIGHT).toInt()
                        maze.rebuild(mazeWidth, mazeHeight, false)
                    }
                    TAG_CELL -> {
                        // read the cell coordinates
                        x = reader.getAttributeValue(null, "x").toInt()
                        y = reader.getAttributeValue(null, "y").toInt()

                        // check if the coordinates belong to the maze grid
                        if (x >= mazeWidth) {
                            errorHandler?.warning(
                                SAXParseException(
                                    "Invalid cell location: x=$x >= $mazeWidth",
                                    reader.location.publicId,
                                    reader.location.systemId,
                                    reader.location.lineNumber,
                                    reader.location.columnNumber
                                )
                            )
                            continue
                        }

                        if (y >= mazeHeight) {
                            errorHandler?.warning(
                                SAXParseException(
                                    "Invalid cell location: y=$y >= $mazeHeight",
                                    reader.location.publicId,
                                    reader.location.systemId,
                                    reader.location.lineNumber,
                                    reader.location.columnNumber
                                )
                            )
                            continue
                        }

                        val walls = maze.cellAt(x, y).walls
                        wallMap.forEach { (attr, direction) ->
                            val value = reader.getAttributeValue(null, attr)

                            // if the attribute for a direction isn't specified, the direction has false by default
                            walls[direction] = value.toBoolean()
                            println(walls[direction])
                        }
                    }
                }
            }
        }
    }

    return maze
}

fun Maze.toJSON(path: String) {
    val mazeJSON = JsonObject()
}

fun main() {
    val errorHandler = object : ErrorHandler {
        override fun warning(exception: SAXParseException) {
            System.err.println(exception.message)
        }

        override fun error(exception: SAXParseException) {
        }

        override fun fatalError(exception: SAXParseException) {
        }

    }
    var maze = Maze(10, 10, true)
    val algo: MazeGenerator = Prim()
    maze = algo.generate(maze.getRegion(), maze)
//    PreviewWindow(maze).showMaze()
//    XMLWriter().write(maze, path)

    validateXML()
    readFromXML(path, errorHandler).show { }
}
