package sudoku.domain.board.cell

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CellPositionTest {
    @Test
    fun `should return correct reference label`() {
        val expectedReferenceLabel = "B2"

        val cellPosition = CellPositionTestBuilder(1, 1).build()

        cellPosition.referenceLabel shouldBe expectedReferenceLabel
    }
}