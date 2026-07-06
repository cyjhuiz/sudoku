package sudoku.domain

import sudoku.domain.board.cell.SudokuCell

// Type aliases for readability and ease of use
typealias SudokuBoard = List<List<SudokuCell>>

typealias RawSudokuBoard = List<List<Int>>

typealias Subgrid = Pair<Int, Int>
