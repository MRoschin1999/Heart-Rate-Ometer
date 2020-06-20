class Observation {
    var mark: Int = 1;
    var board: Array<Array<Int>> = Array(6, { Array(7, { 0 }) });
    var config: Config;

    constructor(config: Config) {
        this.board = Array(config.get_n(), { Array(config.get_m(), { 0 }) })
        this.mark = 1
        this.config = config
    }

    fun nextMove(config: Config, col: Int): piece_drop? {
        var row = 0;
        var is_full = true
        var temp = this.board.copyOf();
        for (i in config.get_n() - 1 downTo 0 step 1) {
            if (temp[i][col] == 0) {
                row = i;
                is_full = false
                break
            }
        }
        if (is_full) {
            return null
        }
        temp[row][col] = this.mark;
        this.board = temp;
        var pieceDrop = piece_drop(row, col, this.mark, checkWin(config));
        this.mark = this.mark % 2 + 1
        return pieceDrop
    }

    private fun checkWin(config: Config): Boolean {
        var res: Boolean
//        Horizontal
        for (row in 0 until config.get_n()) {
            for (col in 0..(config.get_m() - config.get_row_to_win())) {
                res = true
                for (i in 0 until config.get_row_to_win()) {
                    res = res and (this.board[row][col + i] == this.mark)
                }
                if (res) return res
            }
        }
//        Vertical
        for (col in 0 until config.get_m()) {
            for (row in 0..(config.get_n() - config.get_row_to_win())) {
                res = true
                for (i in 0 until config.get_row_to_win()) {
                    res = res and (this.board[row + i][col] == this.mark)
                }
                if (res) return res
            }
        }
//        Diagonal positive
        for (row in 0..(config.get_n() - config.get_row_to_win())) {
            for (col in 0..config.get_m() - config.get_row_to_win()) {
                res = true
                for (i in 0 until config.get_row_to_win()) {
                    res = res and (this.board[row + i][col + i] == this.mark)
                }
                if (res) return res
            }
        }
//        Diagonal negative
        for (row in config.get_n() - 1 downTo config.get_row_to_win() - 1 step 1) {
            for (col in (0..config.get_m() - config.get_row_to_win())) {
                res = true
                for (i in 0 until config.get_row_to_win()) {
                    res = res and (this.board[row - i][col + i] == this.mark)
                }
                if (res) return res
            }
        }
        return false
    }

    fun copyOf(): Observation {
        var i: Int
        var j: Int
        var tmp = Observation(this.config)
        tmp.mark = this.mark
        for (i in 0 until this.config.get_n()) {
            for (j in 0 until this.config.get_m())
                tmp.board[i][j] = this.board[i][j]
        }
        return tmp
    }
}
