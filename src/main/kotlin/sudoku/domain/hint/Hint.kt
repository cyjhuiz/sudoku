package sudoku.domain.hint

import sudoku.domain.board.cell.CellPosition

data class Hint(
    val position: CellPosition,
    val value: Int
) {
    fun message() = "Hint: Cell ${position.referenceLabel} = $value"
}