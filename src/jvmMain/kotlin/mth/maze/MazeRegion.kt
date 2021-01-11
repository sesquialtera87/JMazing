package mth.maze

data class MazeRegion(var x: Int, var y: Int, var width: Int, var height: Int) {

    fun build(x1: Int, y1: Int, x2: Int, y2: Int) = MazeRegion(x1, y1, x2 - x1 + 1, y2 - y1 + 1)
}
