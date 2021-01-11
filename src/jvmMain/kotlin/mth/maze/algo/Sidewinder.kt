package mth.maze.algo

import mth.maze.Maze
import mth.maze.MazeRegion

class Sidewinder : MazeGenerator {

    var carveEastProbability = 0.5

    private fun randomChoice(prob: Double) = random.nextDouble() <= prob

    override fun generate(mazeRegion: MazeRegion, maze: Maze): Maze {
        with(mazeRegion) {
            var corridorStart: Int

            // the first row is a single corridor, since we cannot crave north
            for (i in 0 until width - 1) carvePassage(x + i, 0, Maze.EAST, maze)

            for (j in 1 until height) {
                corridorStart = 0

                for (i in 0 until width) {
                    if (i != width - 1 && randomChoice(carveEastProbability)) {
                        carvePassage(i + x, j + y, Maze.EAST, maze)
                    } else {
                        // choose at random a cell in the corridor and open a passage
                        // towards north
                        val corridorLength = i - corridorStart + 1
                        val northPassagePosition = random.nextInt(corridorLength) + corridorStart
                        carvePassage(x + northPassagePosition, y + j, Maze.NORTH, maze)
                        corridorStart = i + 1
                    }
                }
            }
        }

        return maze
    }
}
