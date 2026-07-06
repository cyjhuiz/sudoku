package sudoku.domain.hint

import sudoku.domain.SudokuBoard
import sudoku.domain.board.cell.CellPosition
import sudoku.domain.board.cell.SudokuCell

class HintManager(
    private val playerBoard: SudokuBoard,
) {
    private val remainingCellPositions = mutableSetOf<CellPosition>()

    private val correctlyFilledCellPositions = mutableSetOf<CellPosition>()

    init {
        playerBoard.forEach { row ->
            row.forEach { cell ->
                if (cell.isModifiable) {
                    remainingCellPositions.add(cell.position)
                }
            }
        }
    }

    fun trackCellUpdate(cell: SudokuCell) {
        if (cell.isCorrectlyFilled()) {
            remainingCellPositions.remove(cell.position)
            correctlyFilledCellPositions.add(cell.position)
        } else {
            remainingCellPositions.add(cell.position)
            correctlyFilledCellPositions.remove(cell.position)
        }
    }

    fun showHint(): Hint? =
        if (remainingCellPositions.iterator().hasNext()) {
            val cellPosition = remainingCellPositions.iterator().next()
            val cell = playerBoard[cellPosition.row][cellPosition.col]
            Hint(
                position = cell.position,
                value = cell.answer
            )
        } else {
            null
        }
}