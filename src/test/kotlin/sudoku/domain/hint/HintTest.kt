package sudoku.domain.hint

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class HintTest {
    @Test
    fun `should return hint message with required details`() {
        val hint =
            HintTestBuilder(
                row = 1,
                col = 1,
                answer = 2
            ).build()

        hint.message() shouldBe "Hint: Cell B2 = 2"
    }
}