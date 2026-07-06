package sudoku.util

import sudoku.domain.SudokuConstants.SUDOKU_SUBGRID_LENGTH

private const val LAST_ALPHABET_IDX = 25
fun Int.toUserRowChar(): Char {
    require(this in 0 .. LAST_ALPHABET_IDX) {
        "Value must be between 0 and $LAST_ALPHABET_IDX (corresponding to A to Z)"
    }
    return ('A'.code + this).toChar()
}

fun Int.toUserIndex() = this + 1

fun Int.toCodeIndex() = this - 1

fun Int.toCodeSubgridIndex() = this/SUDOKU_SUBGRID_LENGTH

fun Int.toFullCodeIndexFromSubgrid() = SUDOKU_SUBGRID_LENGTH * this
