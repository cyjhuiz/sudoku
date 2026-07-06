package sudoku.domain.board

import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.domain.SudokuConstants.NUM_PREFILLED_CELLS
import sudoku.domain.SudokuConstants.SUDOKU_GRID_LENGTH
import sudoku.domain.repository.SudokuSolutionRepository
import sudoku.domain.SudokuBoard

@ExtendWith(MockKExtension::class)
class SudokuBoardFactoryTest {
    @MockK
    lateinit var sudokuSolutionRepository: SudokuSolutionRepository

    lateinit var sudokuBoardFactory: SudokuBoardFactory

    @BeforeEach
    fun setUp() {
        every {
            sudokuSolutionRepository.getRandomSolution()
        } returns solutionBoard

        sudokuBoardFactory = SudokuBoardFactory(sudokuSolutionRepository)
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun `should return board with randomized prefilled positions`() {
        val board1 = sudokuBoardFactory.createRandomBoard()

        val board2 = sudokuBoardFactory.createRandomBoard()

        prefilledPositions(board1) shouldNotBe prefilledPositions(board2)
    }

    @Test
    fun `should return board with correct number of prefilled positions`() {
        val board = sudokuBoardFactory.createRandomBoard()

        val numPrefilledCells = board.flatten().count { !it.isModifiable }

        numPrefilledCells shouldBe NUM_PREFILLED_CELLS
    }

    @Test
    fun `should return board with even distribution of prefilled positions`() {
        val board = sudokuBoardFactory.createRandomBoard()

        val expectedPrefilledPerRow = NUM_PREFILLED_CELLS/SUDOKU_GRID_LENGTH
        board.forEach { row ->
            val numPrefilled = row.count { !it.isModifiable }
            numPrefilled shouldBeGreaterThanOrEqual expectedPrefilledPerRow
        }
    }

    private fun prefilledPositions(board: SudokuBoard) =
        board
            .flatten()
            .filter { !it.isModifiable }
            .map { it.position.row to it.position.col }
            .toSet()

    private companion object {
        val solutionBoard = SudokuSolutionBoardTestBuilder().build()
    }
}