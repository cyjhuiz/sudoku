package sudoku.cli.executor

import sudoku.cli.CliCommandType
import sudoku.domain.SudokuGame
import java.io.PrintStream

interface CliCommandExecutor {
    val output: PrintStream

    val type: CliCommandType

    fun execute(inputCommand: String, sudokuGame: SudokuGame)
}