package mth.maze.io

import com.json_simple.JsonArray
import com.json_simple.JsonKey
import com.json_simple.JsonObject
import com.json_simple.Jsoner
import mth.maze.Maze
import mth.maze.algo.MazeGenerator
import mth.maze.algo.Prim
import mth.maze.show
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import javax.xml.XMLConstants
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent
import javax.xml.transform.stax.StAXSource
import javax.xml.validation.SchemaFactory

const val ATTR_WIDTH = "width"
const val ATTR_HEIGHT = "height"
const val TAG_MAZE = "maze"
const val TAG_CELL = "cell"
const val XML_SCHEMA = "maze_xml_schema.xsd"
const val path = "/home/mattia/IdeaProjects/JMazing/src/jvmMain/kotlin/mth/maze/io/maze_test.xml"

val wallMap = mapOf("north" to Maze.NORTH, "west" to Maze.WEST, "east" to Maze.EAST, "south" to Maze.SOUTH)


fun Maze.toXML(path: String, xmlVersion: String = "1.0") {
    val factory = XMLOutputFactory.newInstance()
    val writer = factory.createXMLStreamWriter(FileOutputStream(path))


    writer.apply {
        writeStartDocument("UTF-8", "1.0")
        writeStartElement(TAG_MAZE)
        writeAttribute("version", xmlVersion)
        writeAttribute(ATTR_WIDTH, "${width}")
        writeAttribute(ATTR_HEIGHT, "${height}")
    }

    for (row in 0 until width)
        for (col in 0 until height) {
            val cell = cellAt(row, col)

            with(writer) {
                writeEmptyElement(TAG_CELL)

                writeAttribute("x", "$row")
                writeAttribute("y", "$col")

                wallMap.forEach { (key, direction) -> writeAttribute(key, cell.walls[direction].toString()) }
            }
        }

    writer.writeEndDocument()
    writer.close()
}

fun validateXML(xmlStreamReader: XMLStreamReader? = null) {
    val reader = xmlStreamReader ?: XMLInputFactory.newInstance().createXMLStreamReader(FileInputStream(path))

    val schema = SchemaFactory
        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .newSchema(JsonKeys::class.java.getResource(XML_SCHEMA))
    schema.newValidator().validate(StAXSource(reader))
}

fun readFromXML(path: String, errorHandler: ErrorHandler? = null): Maze {
    val maze = Maze()
    var mazeWidth = 0
    var mazeHeight = 0

    // cell coordinates
    var x: Int
    var y: Int


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

enum class JsonKeys(override val key: String) : JsonKey {
    CELLS("cells"),
    MAZE_WIDTH("maze_width"),
    MAZE_HEIGHT("maze_height"),
    X("x"),
    Y("y");

    override val value: Any
        get() = ""
}

fun readFromJSON(path: String): Maze {
    val mazeJSON = Jsoner.deserialize(FileReader(path)) as JsonObject
    val width = mazeJSON.getInteger(JsonKeys.MAZE_WIDTH)!!
    val height = mazeJSON.getInteger(JsonKeys.MAZE_HEIGHT)!!
    val maze = Maze(width, height, false)

    mazeJSON.getCollection<Collection<JsonObject>>(JsonKeys.CELLS)?.forEach {
        val x = it.getInteger(JsonKeys.X)!!
        val y = it.getInteger(JsonKeys.Y)!!
        wallMap.forEach { (key, direction) -> maze.cellAt(x, y).walls[direction] = it[key]!!.toString().toBoolean() }
    }

    return maze
}

fun Maze.toJSON(path: String) {
    val cells = JsonArray()
    var walls: BooleanArray

    for (i in 0 until width)
        for (j in 0 until height) {
            walls = cellAt(i, j).walls
            val cell = JsonObject()
            cell.put(JsonKeys.X, i)
            cell.put(JsonKeys.Y, j)
            wallMap.forEach { cell[it.key] = walls[it.value] }
            cells.add(cell)
        }

    val mazeJSON = JsonObject().apply {
        put(JsonKeys.MAZE_WIDTH, width)
        put(JsonKeys.MAZE_HEIGHT, height)
        put(JsonKeys.CELLS, cells)
    }

    FileWriter(path).apply {
        write(mazeJSON.toJson())
        flush()
    }
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
//   maze.toXML(path)

//    validateXML()
//    readFromXML(path, errorHandler).show { }

    maze.toJSON("$path.json")
    readFromJSON("$path.json").show { }
}
