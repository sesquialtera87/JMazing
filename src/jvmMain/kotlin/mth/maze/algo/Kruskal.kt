package mth.maze.algo

import mth.maze.Maze
import mth.maze.MazeRegion
import mth.maze.UnionFind
import mth.maze.algo.MazeGenerator.Companion.DX
import mth.maze.algo.MazeGenerator.Companion.DY

class Kruskal : MazeGenerator {

    lateinit var maze: Maze
    lateinit var edges: ArrayList<IntArray>
    lateinit var region: MazeRegion
    lateinit var set: UnionFind

    fun next() {
        if (edges.isNotEmpty()) {
            val (x, y, direction) = edges.removeAt(random.nextInt(edges.size))
            val x1 = x + DX[direction]!!
            val y1 = y + DY[direction]!!

            println("from ($x,$y) to ($x1,$y1) direction=$direction")
            val cellHash1 = x1 * region.width + y1
            val cellHash2 = x * region.width + y
            println(set.differ(cellHash1, cellHash2))

            if (set.differ(cellHash1, cellHash2)) {
                set.union(set.getNode(cellHash1)!!, set.getNode(cellHash2)!!)
                carvePassage(x, y, direction, maze)
            }
        } else
            println("Nothing to do")
    }

    fun init(mazeRegion: MazeRegion, maze: Maze) {
        this.maze = maze
        this.region = mazeRegion
        this.edges = ArrayList(2 * region.width * region.height)
        this.set = UnionFind(Array(region.width * region.height) { it })

        for (x in 0 until region.width)
            for (y in 0 until region.height) {
                if (y > 0)
                    edges.add(intArrayOf(x, y, Maze.NORTH))
                if (x > 0)
                    edges.add(intArrayOf(x, y, Maze.WEST))
            }

        println(edges.size)
    }

    override fun generate(region: MazeRegion, maze: Maze): Maze {
        with(region) {
            // contains the coordinates of the cell and the direction to follow. The coordinates are RELATIVE to the selection
            val edges: ArrayList<IntArray> = ArrayList(2 * width * height - width * height + 1)
            val set = UnionFind(Array(width * height) { it })

            println(width)
            println(height)

            // initialize the edge collection
            for (x in 0 until width)
                for (y in 0 until height) {
                    if (y > 0)
                        edges.add(intArrayOf(x, y, Maze.NORTH))
                    if (x > 0)
                        edges.add(intArrayOf(x, y, Maze.WEST))
                }

            println(edges)

            while (edges.isNotEmpty()) {
                // pick an edge randomly
                val (x, y, direction) = edges.removeAt(random.nextInt(edges.size))
                val x1 = x + DX[direction]!!
                val y1 = y + DY[direction]!!

                // calculate the hashes of the two connected cells
                val cellHash1 = x1 * width + y1
                val cellHash2 = x * width + y

                if (set.differ(cellHash1, cellHash2)) {
                    val node1 = set.getNode(cellHash1)
                    val node2 = set.getNode(cellHash2)

                    if (node1 != null && node2 != null) {
                        set.union(node1, node2)
                        carvePassage(x + region.x, y + region.y, direction, maze)
                    }
                }
            }
        }

        return maze
    }
}