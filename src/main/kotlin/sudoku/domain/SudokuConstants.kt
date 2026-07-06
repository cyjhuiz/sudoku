package sudoku.domain

import kotlin.math.sqrt

object SudokuConstants {
    const val SUDOKU_GRID_LENGTH = 9
    val SUDOKU_SUBGRID_LENGTH = sqrt(SUDOKU_GRID_LENGTH.toDouble()).toInt()
    const val NUM_PREFILLED_CELLS = 30
}