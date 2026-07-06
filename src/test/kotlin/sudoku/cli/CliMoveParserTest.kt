package sudoku.cli

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import sudoku.cli.util.CliMoveParser
import sudoku.domain.move.MoveTestBuilder

class CliMoveParserTest {

    @Test
    fun `should return move with target value when parsing succeeds`() {
        val expectedMove =
            MoveTestBuilder(
                row = 0,
                col = 0,
                value = 1,
            ).build()

        val result = CliMoveParser.parse("A1 1").shouldBeRight()

        result shouldBe expectedMove
    }

    @Test
    fun `should return move with empty cell value when parsing a clearing move`() {
        val expectedMove =
            MoveTestBuilder(
                row = 0,
                col = 0
            ).build(isClearingMove = true)

        val result = CliMoveParser.parse("A1 clear").shouldBeRight()

        result shouldBe expectedMove
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1", "A1 1 A2 clear"])
    fun `should return failure result when parsing fails due to argument size`(inputMoveArgument: String) {
        val result = CliMoveParser.parse(inputMoveArgument).shouldBeLeft()

        result shouldContain "A move should only have 2 arguments"
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["Z1 1", "31 1"])
    fun `should return failure result when parsing fails due to invalid rowIdx`(inputMoveArgument: String) {
        val result = CliMoveParser.parse(inputMoveArgument).shouldBeLeft()

        result shouldContain "Row character should be between"
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A0 1", "A10 1"])
    fun `should return failure result when parsing fails due to invalid colIdx`(inputMoveArgument: String) {
        val result = CliMoveParser.parse(inputMoveArgument).shouldBeLeft()

        result shouldContain "Column number should be a number between"
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = ["A1 0", "A1 10", "A1 abcd"])
    fun `should return failure result when parsing fails due to invalid value`(inputMoveArgument: String) {
        val result = CliMoveParser.parse(inputMoveArgument).shouldBeLeft()

        result shouldContain "Cell value should be a number between"
    }
}