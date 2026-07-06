package sudoku.domain.board.cell

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE

class SudokuCellTest {

    @Nested
    inner class SetValue {
        private val initialValue = 1
        private val newValue = 2
        @Test
        fun `should return success and set value when cell is modifiable`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = initialValue,
                    isModifiable = true,
                ).build()

            val result = cell.setValue(newValue).shouldBeRight()

            result shouldBe "Move accepted."
            cell.value shouldBe newValue
        }

        @Test
        fun `should return failure result and not set value when cell is not modifiable`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = initialValue,
                    isModifiable = false,
                ).build()

            val result = cell.setValue(newValue).shouldBeLeft()

            result shouldContain "is prefilled"
            cell.value shouldBe initialValue
        }

        @Test
        fun `should return failure result and not set value when incoming value is same as existing value`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = initialValue,
                    isModifiable = true,
                ).build()

            val result = cell.setValue(initialValue).shouldBeLeft()

            result shouldContain "Move value is same as current cell value"
        }
    }

    @Nested
    inner class ClearValue {
        @Test
        fun `should return success and clear value when cell is modifiable`() {
            val cell =
                SudokuCellTestBuilder(
                    isModifiable = true,
                ).build()

            val result = cell.clearValue().shouldBeRight()

            result shouldBe "Move accepted."
            cell.value shouldBe EMPTY_CELL_VALUE
        }

        @Test
        fun `should return failure result and not clear value when cell is not modifiable`() {
            val cell =
                SudokuCellTestBuilder(
                    isModifiable = false,
                ).build()

            val result = cell.clearValue().shouldBeLeft()

            result shouldContain "is prefilled"
            cell.value shouldNotBe EMPTY_CELL_VALUE
        }

        @Test
        fun `should return failure result and not clear value when cell is already empty`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = EMPTY_CELL_VALUE,
                    isModifiable = false,
                ).build()

            val result = cell.clearValue().shouldBeLeft()

            result shouldContain "Cannot clear empty cell"
        }
    }

    @Nested
    inner class IsFilled {
        @Test
        fun `should return true when cell has a filled cell value`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = 1,
                ).build()

            val result = cell.isFilled()

            result shouldBe true
        }

        @Test
        fun `should return false when cell has an empty cell value`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = EMPTY_CELL_VALUE,
                ).build()

            val result = cell.isFilled()

            result shouldBe false
        }
    }

    @Nested
    inner class IsCorrectlyFilled {
        @Test
        fun `should return true when cell value is same as answer`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = 1,
                    answer = 1,
                ).build()

            val result = cell.isCorrectlyFilled()

            result shouldBe true
        }

        @Test
        fun `should return false when cell value is different from answer`() {
            val cell =
                SudokuCellTestBuilder(
                    initialValue = 1,
                    answer = 2,
                ).build()

            val result = cell.isCorrectlyFilled()

            result shouldBe false
        }
    }

    @Nested
    inner class Equals {
        @Test
        fun `should return true when objects are equal`() {
            val cell1 = SudokuCellTestBuilder().build()
            val cell2 = SudokuCellTestBuilder().build()

            val result = cell1 == cell2

            result shouldBe true
        }

        @Test
        fun `should return false when objects are not equal`() {
            val cell1 = SudokuCellTestBuilder(row = 1).build()
            val cell2 = SudokuCellTestBuilder(row = 2).build()

            val result = cell1 == cell2

            result shouldBe false
        }
    }

    @Nested
    inner class HashCode {
        @Test
        fun `should return true when hashcode is equal`() {
            val cell1 = SudokuCellTestBuilder().build()
            val cell2 = SudokuCellTestBuilder().build()

            cell1.hashCode() shouldBe cell2.hashCode()
        }

        @Test
        fun `should return false when hashcode is not equal`() {
            val cell1 = SudokuCellTestBuilder(row = 1).build()
            val cell2 = SudokuCellTestBuilder(row = 2).build()

            cell1.hashCode() shouldNotBe cell2.hashCode()
        }
    }

}