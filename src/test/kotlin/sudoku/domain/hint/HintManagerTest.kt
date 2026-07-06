package sudoku.domain.hint

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import sudoku.domain.board.SudokuPlayerBoardTestBuilder

class HintManagerTest {
    @Test
    fun `should show hint when board is not correct`() {
        val sudokuPlayerBoard =
            SudokuPlayerBoardTestBuilder(
                setOf((ROW to COL))
            ).build()

        val hintManager = HintManager(sudokuPlayerBoard)

        val result = hintManager.showHint()

        result shouldNotBeNull {
            this.position.row shouldBe ROW
            this.position.col shouldBe COL
        }
    }

    @Test
    fun `should show hint when a correct cell becomes wrong again`() {
        val sudokuPlayerBoard =
            SudokuPlayerBoardTestBuilder(
                setOf((ROW to COL))
            ).build()

        val hintManager = HintManager(sudokuPlayerBoard)

        val sudokuCell = sudokuPlayerBoard[ROW][COL]
        sudokuCell.setValue(sudokuCell.answer)
        hintManager.trackCellUpdate(sudokuCell)

        hintManager.showHint() shouldBe null


        val wrongAnswer = sudokuCell.answer+1
        sudokuCell.setValue(wrongAnswer)
        hintManager.trackCellUpdate(sudokuCell)

        val result = hintManager.showHint()

        result shouldNotBeNull {
            this.position.row shouldBe ROW
            this.position.col shouldBe COL
        }
    }

    @Test
    fun `should not show cell as hint when it has been filled correctly`() {
        val sudokuPlayerBoard =
            SudokuPlayerBoardTestBuilder(
                setOf((ROW to COL))
            ).build()

        val hintManager = HintManager(sudokuPlayerBoard)

        val sudokuCell = sudokuPlayerBoard[ROW][COL]
        sudokuCell.setValue(sudokuCell.answer)

        hintManager.trackCellUpdate(sudokuCell)

        hintManager.showHint() shouldBe null
    }



    @Test
    fun `should return null when no more hints are left`() {
        val correctSudokuPlayerBoard =
            SudokuPlayerBoardTestBuilder(
                modifiablePositions = emptySet()
            ).build()

        val hintManager = HintManager(correctSudokuPlayerBoard)

        hintManager.showHint() shouldBe null
    }

    private companion object {
        const val ROW = 0
        const val COL = 0
    }

}