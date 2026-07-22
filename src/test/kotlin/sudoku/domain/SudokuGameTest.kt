package sudoku.domain

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sudoku.domain.board.SudokuBoardFactory
import sudoku.domain.board.cell.SudokuCell
import sudoku.domain.hint.HintManager
import sudoku.domain.hint.HintManagerFactory
import sudoku.domain.move.MoveTestBuilder
import sudoku.domain.violation.ViolationsTracker
import sudoku.domain.violation.ViolationsTrackerFactory

@ExtendWith(MockKExtension::class)
class SudokuGameTest {
    @MockK
    private lateinit var sudokuBoardFactory: SudokuBoardFactory

    @MockK
    private lateinit var hintManagerFactory: HintManagerFactory

    @MockK
    private lateinit var violationsTrackerFactory: ViolationsTrackerFactory

    lateinit var sudokuGame: SudokuGame

    @MockK
    private lateinit var mockSudokuBoard: SudokuBoard

    @RelaxedMockK
    private lateinit var mockHintManager: HintManager

    @RelaxedMockK
    private lateinit var mockViolationsTracker: ViolationsTracker

    @RelaxedMockK
    private lateinit var cell: SudokuCell

    @BeforeEach
    fun setUp() {
        setUpGeneralMocks()

        sudokuGame =
            SudokuGame(
                sudokuBoardFactory,
                hintManagerFactory,
                violationsTrackerFactory,
            )
    }

    @AfterEach
    fun reset() {
        clearAllMocks()
    }

    @Nested
    inner class StartNewGame {
        @Test
        fun `should start new game`() {
            every {
                cell.setValue(any())
            } returns SUCCESS_MSG.right()

            // simulate ongoing game before starting new game
            sudokuGame.place(move)
            sudokuGame.hasMadeFirstMove shouldBe true

            sudokuGame.startNewGame()

            sudokuGame.hasMadeFirstMove shouldBe false
            // 2 times as its being called during initialisation and startNewGame()
            verify(exactly = 2) {
                sudokuBoardFactory.createRandomBoard()
                hintManagerFactory.create(mockSudokuBoard)
                violationsTrackerFactory.create(mockSudokuBoard)
            }
        }
    }

    @Nested
    inner class Place {
        @Test
        fun `should return success result and track changes when cell updates successfully`() {
            every {
                cell.setValue(any())
            } returns SUCCESS_MSG.right()

            val result = sudokuGame.place(move).shouldBeRight()

            result shouldBe SUCCESS_MSG

            sudokuGame.hasMadeFirstMove shouldBe true
            verify(exactly = 1) {
                cell.setValue(move.value)
                mockHintManager.trackCellUpdate(cell)
                mockViolationsTracker.trackCellUpdate(cell, INITIAL_VALUE)
            }
        }

        @Test
        fun `should return failure result when cell updates fail`() {
            every {
                cell.setValue(any())
            } returns FAILURE_MSG.left()

            val result = sudokuGame.place(move).shouldBeLeft()

            result shouldBe FAILURE_MSG
            verify(exactly = 1) {
                cell.setValue(move.value)
            }

            verify {
                mockHintManager wasNot called
                mockViolationsTracker wasNot called
            }
        }


    }

    @Nested
    inner class Clear {
        @Test
        fun `should clear move, update cell and track hint and violation changes`() {
            every {
                cell.clearValue()
            } returns SUCCESS_MSG.right()

            val result = sudokuGame.clear(move).shouldBeRight()

            result shouldBe SUCCESS_MSG
            verify(exactly = 1) {
                cell.clearValue()
                mockHintManager.trackCellUpdate(any())
                mockViolationsTracker.trackCellUpdate(any(), any())
            }
        }

        @Test
        fun `should return failure result when trying to clearing a cell fails`() {
            every {
                cell.clearValue()
            } returns FAILURE_MSG.left()

            val result = sudokuGame.clear(move).shouldBeLeft()

            result shouldBe FAILURE_MSG
            verify(exactly = 1) {
                cell.clearValue()
            }
            verify {
                mockHintManager wasNot called
                mockViolationsTracker wasNot called
            }
        }
    }

    private fun setUpGeneralMocks() {
        every {
            sudokuBoardFactory.createRandomBoard()
        } returns mockSudokuBoard

        every {
            hintManagerFactory.create(any())
        } returns mockHintManager

        every {
            violationsTrackerFactory.create(any())
        } returns mockViolationsTracker

        every {
            mockSudokuBoard[move.position.row][move.position.col]
        } returns cell
        every {
            cell.value
        } returns INITIAL_VALUE
    }

    private companion object {
        const val INITIAL_VALUE = 1
        val move =
            MoveTestBuilder(
                value = INITIAL_VALUE + 1,
            ).build()

        const val SUCCESS_MSG = "success"
        const val FAILURE_MSG = "failure"
    }
}