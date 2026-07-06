package sudoku.domain.repository

import sudoku.domain.RawSudokuBoard

interface SudokuSolutionRepository {
    fun getRandomSolution(): RawSudokuBoard
}