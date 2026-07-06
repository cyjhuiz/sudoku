package sudoku.domain.hint

import sudoku.domain.board.cell.CellPositionTestBuilder

class HintTestBuilder(
    private val row: Int = 0,
    private val col: Int = 0,
    private val answer: Int = 1
) {
    fun build() =
        Hint(
            position = CellPositionTestBuilder(row, col).build(),
            value = answer,
        )
}