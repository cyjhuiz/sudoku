package sudoku.cli

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.cli.util.systemExit
import sudoku.domain.SudokuGame
import sudoku.domain.board.SudokuBoardFactory
import sudoku.domain.board.SudokuPlayerBoardTestBuilder
import sudoku.domain.board.cell.SudokuCell
import sudoku.domain.hint.HintManagerFactory
import sudoku.domain.violation.ViolationsTrackerFactory
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.Duration

@ExtendWith(MockKExtension::class)
class SudokuCliIntegrationTest {
    // only mocking sudokuBoardFactory for easier testing to make board values deterministic
    @MockK
    private lateinit var sudokuBoardFactory: SudokuBoardFactory

    private lateinit var hintManagerFactory: HintManagerFactory

    private lateinit var violationsTrackerFactory: ViolationsTrackerFactory

    private lateinit var sudokuGame: SudokuGame

    private lateinit var outputStream: ByteArrayOutputStream

    private lateinit var output: PrintStream

    @BeforeEach
    fun setUp() {
        hintManagerFactory = HintManagerFactory()
        violationsTrackerFactory = ViolationsTrackerFactory()

        outputStream = ByteArrayOutputStream()
        output = PrintStream(outputStream, true)

        mockkStatic(::systemExit)
        every { systemExit(any()) } just Runs
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun `should place move via command`() {
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns nearlyCompletedBoard()

        sudokuGame = SudokuGame(sudokuBoardFactory, hintManagerFactory, violationsTrackerFactory)


        val placeCommand = "A1 1\n"
        val input = input(placeCommand)

        val sudokuCli = SudokuCli(sudokuGame, input, output)
        sudokuCli.run()

        val response = gameResponseFromEnd(outputStream)
        response shouldContain "A 1"
    }

    @Test
    fun `should clear move via command`() {
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns nearlyCompletedBoard()

        sudokuGame = SudokuGame(sudokuBoardFactory, hintManagerFactory, violationsTrackerFactory)

        val placeCommand = "A1 1\n"
        val clearCommand = "A1 clear\n"
        val input = input(placeCommand + clearCommand)

        val sudokuCli = SudokuCli(sudokuGame, input, output)
        sudokuCli.run()

        val response = gameResponseFromEnd(outputStream)
        response shouldNotContain "A 1"
    }

    @Test
    fun `should show hint via command`() {
        val nearlyCompletedBoard = nearlyCompletedBoard()
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns nearlyCompletedBoard

        val remainingCell = nearlyCompletedBoard[CELL_A1_ROW][CELL_A1_COL]
        sudokuGame = SudokuGame(sudokuBoardFactory, hintManagerFactory, violationsTrackerFactory)

        val hintCommand = "hint\n"
        val input = input(hintCommand)

        val sudokuCli = SudokuCli(sudokuGame, input, output)
        sudokuCli.run()

        val response = gameResponseFromEnd(outputStream)
        response shouldContain "Hint: Cell ${remainingCell.position.referenceLabel} = ${remainingCell.answer}"
    }

    @Test
    fun `should check violations via command`() {
        val nearlyCompletedBoard = nearlyCompletedBoard()
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns nearlyCompletedBoard

        sudokuGame = SudokuGame(sudokuBoardFactory, hintManagerFactory, violationsTrackerFactory)

        val remainingCell = nearlyCompletedBoard[CELL_A1_ROW][CELL_A1_COL]
        val invalidValue = randomInvalidValue(remainingCell)

        val placeCommand = "A1 $invalidValue\n"
        val checkCommand = "check\n"
        val input = input(placeCommand + checkCommand)

        val sudokuCli = SudokuCli(sudokuGame, input, output)
        sudokuCli.run()

        val raceConditionGracePeriod = Duration.ofSeconds(3)
        await().atMost(raceConditionGracePeriod).untilAsserted {
            // actual response is 2nd last as an additional round starts after checking
            val response = gameResponseFromEnd(outputStream, offset = 1)
            response shouldContain "already exists"
        }

    }

    @Test
    fun `should exit via command`() {
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns nearlyCompletedBoard()

        sudokuGame = SudokuGame(sudokuBoardFactory, hintManagerFactory, violationsTrackerFactory)

        val exitCommand = "exit\n"
        val input = input(exitCommand)

        val sudokuCli = SudokuCli(sudokuGame, input, output)
        sudokuCli.run()

        val response = gameResponseFromEnd(outputStream)
        response shouldContain "Exiting game..."
        verify(exactly = 1) {
            systemExit(0)
        }
    }

    private fun nearlyCompletedBoard(lastCellPosition: Pair<Int, Int> = cellA1Position) =
        SudokuPlayerBoardTestBuilder(
            modifiablePositions = setOf(lastCellPosition),
        ).build()

    private fun gameResponseFromEnd(outputStream: ByteArrayOutputStream, offset: Int = 0): String {
        val responses = outputStream.toString().split(NEW_ROUND_DELIMITER)
        return responses[responses.lastIndex - offset]
    }

    private fun randomInvalidValue(cell: SudokuCell) =
        (1..9)
            .filter { it != cell.answer }
            .random()

    private fun input(command: String) =
        BufferedReader(
            ByteArrayInputStream(command.toByteArray()).reader()
        )

    private companion object {
        const val CELL_A1_ROW = 0
        const val CELL_A1_COL = 0
        val cellA1Position = (CELL_A1_ROW to CELL_A1_COL)

        const val NEW_ROUND_DELIMITER = "Current grid:"
    }
}