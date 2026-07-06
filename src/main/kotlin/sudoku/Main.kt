package sudoku

import sudoku.cli.SudokuCli
import sudoku.domain.SudokuGame
import sudoku.domain.board.SudokuBoardFactory
import sudoku.domain.hint.HintManagerFactory
import sudoku.domain.violation.ViolationsTrackerFactory
import sudoku.persistence.LocalSudokuSolutionRepositoryImpl


fun main() {
    val sudokuSolutionRepository = LocalSudokuSolutionRepositoryImpl()
    val sudokuBoardFactory = SudokuBoardFactory(sudokuSolutionRepository)

    val hintManagerFactory = HintManagerFactory()
    val violationsTrackerFactory = ViolationsTrackerFactory()

    val sudokuGame =
        SudokuGame(
            sudokuBoardFactory,
            hintManagerFactory,
            violationsTrackerFactory,
        )

    SudokuCli(sudokuGame).run()
}