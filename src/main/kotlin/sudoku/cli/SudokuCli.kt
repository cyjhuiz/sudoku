package sudoku.cli

import sudoku.cli.executor.CheckCommandExecutor
import sudoku.cli.executor.ClearCommandExecutor
import sudoku.cli.executor.ExitCommandExecutor
import sudoku.cli.executor.CliCommandExecutor
import sudoku.cli.executor.HintCommandExecutor
import sudoku.cli.executor.PlaceCommandExecutor
import sudoku.cli.util.systemExit
import sudoku.domain.SudokuConstants.SUDOKU_GRID_LENGTH
import sudoku.domain.SudokuGame
import sudoku.util.getOrThrow
import sudoku.util.toUserRowChar
import java.io.BufferedReader
import java.io.PrintStream

class SudokuCli(
    private val sudokuGame: SudokuGame,
    private val input: BufferedReader = System.`in`.bufferedReader(),
    private val output: PrintStream = System.out,
) {
    private val type2CommandExecutor =
        listOf(
            PlaceCommandExecutor(output),
            ClearCommandExecutor(output),
            CheckCommandExecutor(output),
            HintCommandExecutor(output),
            ExitCommandExecutor(output)
        ).associateBy { it.type.name }

    init {
        // ensure every command type has an executor before the game starts
        require(
            CliCommandType.entries.map { it.name }.toSet() == type2CommandExecutor.keys
        )
    }

    fun run() {
        try {
            runGame()
        } catch (e: Exception) {
            e.printStackTrace()
            output.println("Shutting down game as an unexpected exception occurred: ${e.message}")
            systemExit(1)
        }
    }

    private fun runGame() {
        output.println("Welcome to Sudoku!")
        while (true) {
            displayStartingMessage(sudokuGame)
            displayPlayerBoard(sudokuGame)

            if (sudokuGame.hasCompleted()) {
                showCompletionAndRestart()
                continue
            }

            val inputCommand = input.readLine() ?: return
            val cliCommandType = CliCommandType.from(inputCommand)
            val commandExecutor = type2CommandExecutor.getOrThrow<CliCommandExecutor>(cliCommandType.name)

            commandExecutor.execute(inputCommand, sudokuGame)
        }
    }

    private fun displayStartingMessage(sudokuGame: SudokuGame) {
        // extra println to separate new rounds easily
        println()
        if (sudokuGame.hasMadeFirstMove) {
            output.println("Current grid:")
        } else {
            output.println("Here is your puzzle:")
        }
    }

    private fun displayPlayerBoard(sudokuGame: SudokuGame) {
        val playerBoard = sudokuGame.playerBoard

        output.println(COLUMN_HEADER)
        playerBoard.forEachIndexed { rowIdx, row ->
            val rowValues =
                row.joinToString(" ") { cell ->
                    if (cell.isFilled()) cell.value.toString() else EMPTY_CELL_SYMBOL
                }

            output.println("${rowIdx.toUserRowChar()} " + rowValues)
        }
    }

    private fun showCompletionAndRestart() {
        output.println("You have successfully completed the Sudoku puzzle!")
        output.println("Enter any key to play again...")
        input.readLine()

        output.println("Starting new game...")
        sudokuGame.startNewGame()
    }

    private companion object {
        val COLUMN_HEADER = "  ${(1 .. SUDOKU_GRID_LENGTH).joinToString(" ")}"
        const val EMPTY_CELL_SYMBOL = "_"
    }
}