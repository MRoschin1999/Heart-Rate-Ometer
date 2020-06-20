class Config {
    private var n: Int = 6;
    private var m: Int = 7;
    private var row_to_win: Int = 4;

    constructor(n: Int, m: Int, row_to_win: Int) {
        this.n = n;
        this.m = m;
        this.row_to_win = row_to_win;
    }

    fun get_n(): Int {
        return this.n;
    }

    fun get_m(): Int {
        return this.m;
    }

    fun get_row_to_win(): Int {
        return this.row_to_win;
    }
}