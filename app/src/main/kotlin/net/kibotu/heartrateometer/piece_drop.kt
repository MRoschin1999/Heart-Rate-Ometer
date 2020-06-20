class piece_drop {
    var row: Int = 0;
    var col: Int = 0;
    var mark: Int = 1;
    var win: Boolean = false;

    constructor(row: Int, col: Int, mark: Int, win: Boolean) {
        this.row = row;
        this.col = col;
        this.mark = mark;
        this.win = win;
    }

    fun get_row(): Int {
        return this.row;
    }

    fun get_col(): Int {
        return this.col;
    }

    fun get_mark(): Int {
        return this.mark;
    }
}