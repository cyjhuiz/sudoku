package sudoku.domain.violation

import sudoku.domain.SudokuBoard

class ViolationsTrackerFactory {
    fun create(playerBoard: SudokuBoard): ViolationsTracker =
        ViolationsTracker(playerBoard)
}