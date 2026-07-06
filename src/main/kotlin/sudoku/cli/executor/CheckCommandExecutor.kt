package sudoku.cli.executor

import sudoku.cli.CliCommandType
import sudoku.domain.SudokuGame
import java.io.PrintStream

class CheckCommandExecutor(
    override val output: PrintStream
) : CliCommandExecutor {
    override val type = CliCommandType.CHECK

    override fun execute(inputCommand: String, sudokuGame: SudokuGame) {
        sudokuGame.checkViolationResult()
            .onRight { successMessage ->
                output.println(successMessage)
            }.onLeft { errorMessages ->
                errorMessages.forEach { output.println(it) }
            }
    }
}