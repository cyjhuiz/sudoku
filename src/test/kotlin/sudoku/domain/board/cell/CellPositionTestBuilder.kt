package sudoku.domain.board.cell

class CellPositionTestBuilder(
    private val row: Int = 0,
    private val col: Int = 0,
) {
    fun build() = CellPosition(row, col)
}