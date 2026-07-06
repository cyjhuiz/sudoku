package sudoku.cli.executor

import arrow.core.left
import arrow.core.right
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.domain.SudokuGame
import sudoku.domain.move.MoveTestBuilder
import java.io.PrintStream

@ExtendWith(MockKExtension::class)
class ClearCommandExecutorTest {
    @RelaxedMockK
    private lateinit var sudokuGame: SudokuGame

    @RelaxedMockK
    private lateinit var output: PrintStream

    private lateinit var clearCommandExecutor: ClearCommandExecutor

    @BeforeEach
    fun setUp() {
        clearCommandExecutor = ClearCommandExecutor(output)
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `should clear move and print success result`() {
        every {
            sudokuGame.clear(any())
        } returns GAME_SUCCESS_MSG.right()

        clearCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.clear(move)
            output.println(GAME_SUCCESS_MSG)
        }
    }

    @Test
    fun `should print failure result when clear move failed`() {
        every {
            sudokuGame.clear(any())
        } returns GAME_FAILURE_MESSAGE.left()

        clearCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.clear(move)
            output.println(GAME_FAILURE_MESSAGE)
        }
    }

    @Test
    fun `should print failure result when parsing errors exists`() {
        clearCommandExecutor.execute(
            inputCommand = "Z10 clear",
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            output.println(
                match<String> { it.contains("Invalid move") }
            )
        }
        verify {
            sudokuGame wasNot called
        }
    }

    private companion object {
        const val INPUT_COMMAND = "A1 clear"
        val move =
            MoveTestBuilder()
                .build(isClearingMove = true)

        const val GAME_SUCCESS_MSG = "success"
        const val GAME_FAILURE_MESSAGE = "failure"
    }
}