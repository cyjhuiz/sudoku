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
class PlaceCommandExecutorTest {
    @RelaxedMockK
    private lateinit var sudokuGame: SudokuGame

    @RelaxedMockK
    private lateinit var output: PrintStream

    private lateinit var placeCommandExecutor: PlaceCommandExecutor

    @BeforeEach
    fun setUp() {
        placeCommandExecutor = PlaceCommandExecutor(output)
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `should place move successfully`() {
        every {
            sudokuGame.place(any())
        } returns SUCCESS_MESSAGE.right()

        placeCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.place(move)
            output.println(SUCCESS_MESSAGE)
        }
    }

    @Test
    fun `should print failure result when place move failed`() {
        every {
            sudokuGame.place(any())
        } returns FAILURE_RESULT.left()

        placeCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.place(move)
            output.println(FAILURE_RESULT)
        }
    }

    @Test
    fun `should print failure result when parsing errors exists`() {
        placeCommandExecutor.execute(
            inputCommand = "Z10 10",
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
        const val INPUT_COMMAND = "A1 1"
        val move = MoveTestBuilder().build()

        const val SUCCESS_MESSAGE = "sucess"
        const val FAILURE_RESULT = "failure"
    }
}