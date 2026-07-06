package sudoku.cli.executor

import arrow.core.left
import arrow.core.right
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
import java.io.PrintStream

@ExtendWith(MockKExtension::class)
class CheckCommandExecutorTest {
    @RelaxedMockK
    private lateinit var sudokuGame: SudokuGame

    @RelaxedMockK
    private lateinit var output: PrintStream

    private lateinit var checkCommandExecutor: CheckCommandExecutor

    @BeforeEach
    fun setUp() {
        checkCommandExecutor = CheckCommandExecutor(output)
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `should execute violation check and print success result when no violations exist`() {
        every {
            sudokuGame.checkViolationResult()
        } returns SUCCESS_MESSAGE.right()

        checkCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.checkViolationResult()
            output.println(SUCCESS_MESSAGE)
        }
    }

    @Test
    fun `should execute violation check and print failure result when violations exist`() {
        every {
            sudokuGame.checkViolationResult()
        } returns errorMessages.left()

        checkCommandExecutor.execute(
            inputCommand = INPUT_COMMAND,
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            sudokuGame.checkViolationResult()
        }
        verify(exactly = errorMessages.size) {
            output.println(ERROR_MESSAGE)
        }
    }

    private companion object {
        const val INPUT_COMMAND = "INPUT_COMMAND"
        const val SUCCESS_MESSAGE = "success"
        const val ERROR_MESSAGE = "error"
        val errorMessages = listOf(ERROR_MESSAGE, ERROR_MESSAGE)
    }
}