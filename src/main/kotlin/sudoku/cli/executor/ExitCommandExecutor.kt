package sudoku.cli.executor

import sudoku.cli.CliCommandType
import sudoku.cli.util.systemExit
import sudoku.domain.SudokuGame
import java.io.PrintStream

class ExitCommandExecutor(
    override val output: PrintStream
) : CliCommandExecutor {
    override val type = CliCommandType.EXIT

    override fun execute(inputCommand: String, sudokuGame: SudokuGame) {
        output.println("Exiting game...")
        systemExit(0)
    }
}