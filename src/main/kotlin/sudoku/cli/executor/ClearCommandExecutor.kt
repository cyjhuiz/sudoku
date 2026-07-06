package sudoku.cli.executor

import arrow.core.flatMap
import sudoku.cli.CliCommandType
import sudoku.cli.util.CliMoveParser
import sudoku.domain.SudokuGame
import java.io.PrintStream

class ClearCommandExecutor(
    override val output: PrintStream,
) : CliCommandExecutor {
    override val type = CliCommandType.CLEAR

    override fun execute(inputCommand: String, sudokuGame: SudokuGame) {
        CliMoveParser.parse(inputCommand)
            .flatMap { move ->
                sudokuGame.clear(move)
            }.onRight { successMessage ->
                output.println(successMessage)
            }.onLeft { errorMessage ->
                output.println(errorMessage)
            }
    }
}