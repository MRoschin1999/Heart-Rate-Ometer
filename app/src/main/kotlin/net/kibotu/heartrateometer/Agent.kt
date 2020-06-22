package net.kibotu.heartrateometer

import Config
import Observation
import piece_drop

class Agent {
    var A = 10000000
    var B = 1000
    var C = 10
    var D = -1000
    var E = -100000

    var config: Config

    constructor(config: Config) {
        this.config = config
    }

    fun rand_from_max(array: Array<Int>): Int {
        var m: Int = array.max() ?: -1
        var i: Int
        var res: MutableSet<Int> = mutableSetOf()
        for (i in 0..6) {
            if (array[i] == m) {
                val add = res.add(i)
            }
        }
        return res.random()
    }

    fun calculate_the_move(level: Int?, observation: Observation): Int {
        var obs: Observation
        var piece: piece_drop?
        when (level) {
            1 -> {
                var i: Int
                for (i in 0 until observation.config.get_m()) {
                    obs = observation.copyOf()
                    piece = obs.nextMove(config, i)
                    if (piece == null) continue
                    if (piece.win) return i
                }
                for (i in 0 until observation.config.get_m()) {
                    obs = observation.copyOf()
                    obs.mark = obs.mark % 2 + 1
                    piece = obs.nextMove(config, i)
                    if (piece == null) continue
                    if (piece.win) return i
                }
                return (0..config.get_m()).random()
            }
            2 -> {
                var i: Int
                for (i in 0 until observation.config.get_m()) {
                    obs = observation.copyOf()
                    piece = obs.nextMove(config, i)
                    if (piece == null) continue
                    if (piece.win) return i
                }
                for (i in 0 until observation.config.get_m()) {
                    obs = observation.copyOf()
                    obs.mark = obs.mark % 2 + 1
                    piece = obs.nextMove(config, i)
                    if (piece == null) continue
                    if (piece.win) return i
                }
                var weights: Array<Int> = Array(7, { 0 })
                for (i in 0 until observation.config.get_m()) {
                    obs = observation.copyOf()
                    piece = obs.nextMove(config, i)
                    if (piece == null) weights[i] = -10000000
                    weights[i] = value_the_situation(obs)
                }
                val maxIdx = rand_from_max(weights)
                return maxIdx
            }
//            3 -> {
//                var i: Int
//                var tmp: piece_drop
//                obs = observation.copyOf()
//                var tree = Node(0, obs.board)
//                for (i in 0 until config.get_m()) {
//                    obs = observation.copyOf()
//                    tmp = obs.nextMove(config, i)!!
//                    tree.leaves?.set(i, Node(value_the_situation(obs), obs.board))
//                }
//                for (i in 0 until config.get_m()) {
//
//                }
            }
        return 0
    }

    fun value_the_situation(observation: Observation): Int {
        return A * count_the_ns(observation, 4, 2) +
                B * count_the_ns(observation, 3, 2) +
                C * count_the_ns(observation, 2, 2) +
                D * count_the_ns(observation, 2, 1) +
                E * count_the_ns(observation, 3, 1)
    }

    fun count_the_ns(observation: Observation, n: Int, mark: Int): Int {
        var res: Boolean
        var counter = 0
        var temp = observation.board.copyOf()
        for (row in 0 until config.get_n()) {
            for (col in 0..(config.get_m() - n)) {
                res = true
                for (i in 0 until n) {
                    res = res and (observation.board[row][col + i] == mark)
                }
                if (res) counter++
            }
        }
//        Vertical
        for (col in 0 until config.get_m()) {
            for (row in 0..(config.get_n() - n)) {
                res = true
                for (i in 0 until n) {
                    res = res and (observation.board[row + i][col] == mark)
                }
                if (res) counter++
            }
        }
//        Diagonal positive
        for (row in 0..(config.get_n() - n)) {
            for (col in 0..config.get_m() - n) {
                res = true
                for (i in 0 until n) {
                    res = res and (observation.board[row + i][col + i] == mark)
                }
                if (res) counter++
            }
        }
//        Diagonal negative
        for (row in config.get_n() - 1 downTo n - 1 step 1) {
            for (col in (0..config.get_m() - n)) {
                res = true
                for (i in 0 until n) {
                    res = res and (observation.board[row - i][col + i] == mark)
                }
                if (res) counter++
            }
        }
        return counter
    }
}
