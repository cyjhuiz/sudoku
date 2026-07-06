package sudoku.domain.hint

import sudoku.domain.SudokuBoard

class HintManagerFactory {
    fun create(playerBoard: SudokuBoard): HintManager =
        HintManager(playerBoard)
}