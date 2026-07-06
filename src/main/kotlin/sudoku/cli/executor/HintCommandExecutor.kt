package sudoku.cli.executor

import sudoku.cli.CliCommandType
import sudoku.domain.SudokuGame
import java.io.PrintStream

class HintCommandExecutor(
    override val output: PrintStream
) : CliCommandExecutor {
    override val type = CliCommandType.HINT

    override fun execute(inputCommand: String, sudokuGame: SudokuGame) {
        val hint = sudokuGame.showHint()
        if (hint == null) {
            output.println("Game completed, no more hints available")
            return
        }
        output.println(hint.message())
    }
}