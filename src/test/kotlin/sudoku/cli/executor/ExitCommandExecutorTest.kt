package sudoku.cli.executor

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.cli.util.systemExit
import sudoku.domain.SudokuGame
import java.io.PrintStream

@ExtendWith(MockKExtension::class)
class ExitCommandExecutorTest {
    @RelaxedMockK
    private lateinit var sudokuGame: SudokuGame

    @RelaxedMockK
    private lateinit var output: PrintStream

    @BeforeEach
    fun setUp() {
        mockkStatic(::systemExit)
        every { systemExit(any()) } just Runs
    }

    @AfterEach
    fun reset() {
        unmockkAll()
    }

    @Test
    fun `should exit with message`() {
        ExitCommandExecutor(output).execute(
            inputCommand = "exit",
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            output.println("Exiting game...")
            systemExit(0)
        }
    }
}