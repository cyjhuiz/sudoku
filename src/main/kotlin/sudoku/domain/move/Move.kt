package sudoku.domain.move

import sudoku.domain.board.cell.CellPosition

data class Move(
    val position: CellPosition,
    val value: Int
)