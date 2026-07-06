package sudoku.domain.board

import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE
import sudoku.domain.board.cell.SudokuCellTestBuilder

class SudokuPlayerBoardTestBuilder(
    private val modifiablePositions: Set<Pair<Int, Int>> = emptySet(),
) {
    private val sudokuSolutionBoard = SudokuSolutionBoardTestBuilder().build()

    fun build(isFullyModifiableBoard: Boolean = false) =
        sudokuSolutionBoard.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, value ->
                val isModifiable =
                    isFullyModifiableBoard || (rowIdx to colIdx) in modifiablePositions

                SudokuCellTestBuilder(
                    row = rowIdx,
                    col = colIdx,
                    initialValue =
                        if (isModifiable) {
                            EMPTY_CELL_VALUE
                        } else {
                            value
                        },
                    isModifiable = isModifiable
                ).build()
            }
        }
}