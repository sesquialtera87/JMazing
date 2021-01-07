package mth.maze

import java.util.*

/**
 * Implements a union find structure that uses union by rank and path
 * compression. The union by rank guarantees worst case find time of O(log N),
 * while Tarjan shows that in combination with path compression (halving) the
 * average time for an arbitrary sequence of m >= n operations is
 * O(m*alpha(m,n)), where alpha is the inverse of the Ackermann function,
 * defined as follows:
 * `alpha(m,n) = min{i >= 1 | A(i, floor(m/n)) > log n} for m >= n >= 1`
 * Which yields almost constant time for each individual operation.
 */
class UnionFind(elements: Array<Any>) {
    /**
     * Maps from elements to nodes
     */
    private var nodes: MutableMap<Any, Node> = Hashtable()

    /**
     * Returns the node that represents element.
     */
    fun getNode(element: Any): Node? {
        return nodes[element]
    }

    /**
     * Returns the set that contains `node`. This implementation
     * provides path compression by halving.
     */
    fun find(node: Node): Node {
        var node = node

        while (node.parent.parent !== node.parent) {
            val t = node.parent.parent
            node.parent = t
            node = t
        }

        return node.parent
    }

    /**
     * Unifies the sets `a` and `b` in constant time
     * using a union by rank on the tree size.
     */
    fun union(a: Node, b: Node) {
        val set1 = find(a)
        val set2 = find(b)

        if (set1 !== set2) {
            // Limits the worst case runtime of a find to O(log N)
            if (set1.size < set2.size) {
                set2.parent = set1
                set1.size = set1.size + set2.size
            } else {
                set1.parent = set2
                set2.size = set1.size + set2.size
            }
        }
    }

    /**
     * Returns true if element a and element b are not in the same set. This
     * uses getNode and then find to determine the elements set.
     *
     * @param a The first element to compare.
     * @param b The second element to compare.
     * @return Returns true if a and b are in the same set.
     * @see .getNode
     */
    fun differ(a: Any, b: Any): Boolean {
        val set1 = getNode(a)?.let { find(it) }
        val set2 = getNode(b)?.let { find(it) }
        return set1 !== set2
    }

    /**
     * A class that defines the identity of a set.
     */
    class Node {
        /**
         * Reference to the parent node. Root nodes point to themselves.
         */
        var parent = this

        /**
         * The size of the tree. Initial value is 1.
         */
        var size = 1
    }

    /**
     * Constructs a union find structure and initializes it with the specified
     * elements.
     */
    init {
        for (i in elements.indices) {
            nodes[elements[i]] = Node()
        }
    }
}