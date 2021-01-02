package mth.jmazex.algo

import kotlin.reflect.KClass

enum class Algorithms(private val generatorClass: KClass<out MazeGenerator>) {

    RECURSIVE_BACKTRACKING(RecursiveBacktracking::class),
    HUNT_AND_KILL(HuntAndKill::class),

    //    RECURSIVE_DIVISION,
    BINARY_TREE(BinaryTree::class);

    val generator: MazeGenerator by lazy { generatorClass.java.getDeclaredConstructor().newInstance() }

}