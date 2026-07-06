package sudoku.cli.executor

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.domain.SudokuGame
import sudoku.domain.hint.HintTestBuilder
import java.io.PrintStream

@ExtendWith(MockKExtension::class)
class HintCommandExecutorTest {
    @RelaxedMockK
    private lateinit var sudokuGame: SudokuGame

    @RelaxedMockK
    private lateinit var output: PrintStream

    private lateinit var hintCommandExecutor: HintCommandExecutor

    @BeforeEach
    fun setUp() {
        hintCommandExecutor = HintCommandExecutor(output)
    }

    @Test
    fun `should print hint when hint exists`() {
        every { sudokuGame.showHint() } returns hint

        hintCommandExecutor.execute(
            inputCommand = "exit",
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            output.println(hint.message())
        }
    }

    @Test
    fun `should print no more hints when hint does not exist`() {
        every { sudokuGame.showHint() } returns null

        hintCommandExecutor.execute(
            inputCommand = "exit",
            sudokuGame = sudokuGame,
        )

        verify(exactly = 1) {
            output.println(
                match<String> { it.contains("no more hints") }
            )
        }
    }

    private companion object {
        val hint = HintTestBuilder().build()
    }
}