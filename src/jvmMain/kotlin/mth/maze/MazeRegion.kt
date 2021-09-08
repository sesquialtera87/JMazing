package mth.maze

/**
 * A rectangular region into the maze area. The region in determined by the upper-left corner and by
 * its width and height
 */
data class MazeRegion(var x: Int, var y: Int, var width: Int, var height: Int) {

    companion object {

        /**
         * Construct a region from the upper-left and the bottom-right corners
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         */
        fun build(x1: Int, y1: Int, x2: Int, y2: Int) = MazeRegion(x1, y1, x2 - x1 + 1, y2 - y1 + 1)
    }
}
