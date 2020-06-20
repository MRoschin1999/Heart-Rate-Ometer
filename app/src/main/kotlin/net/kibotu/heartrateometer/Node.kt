package net.kibotu.heartrateometer

class Node {
    var weight: Int
    var leaves: Array<Node?>
    var root: Node? = null



    constructor(weight: Int) {
        this.weight = weight
        this.leaves = Array(7, { null })
    }

    constructor(weight: Int, root: Node) {
        this.weight = weight
        this.leaves = Array(7, { null })
    }

    fun next(n: Int): Node? {
        return leaves?.get(n)
    }

    fun prev(): Node? {
        return root
    }

    fun get_weight(): Int {
        return this.weight
    }

    fun add_next_step(weights: Array<Int>) {
        var i: Int
        for (i in 0..6){
            this.leaves[i] = Node(weights[i], this)
        }
    }
}