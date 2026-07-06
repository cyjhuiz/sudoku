package sudoku.domain.move

import sudoku.domain.board.cell.CellPositionTestBuilder
import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE

class MoveTestBuilder(
    private val row: Int = 0,
    private val col: Int = 0,
    private val value: Int = 1
) {
    fun build(isClearingMove: Boolean = false) =
        Move(
            position = CellPositionTestBuilder(row, col).build(),
            value = if (isClearingMove) EMPTY_CELL_VALUE else value,
        )
}