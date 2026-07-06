package sudoku.cli

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class CliCommandTypeTest {

    @ParameterizedTest(name = "{0}={1}")
    @MethodSource("commandTypeProvider")
    fun `should return correct enum based on input command`(
        inputCommand: String,
        expectedType: CliCommandType
    ) {
        val actualType = CliCommandType.from(inputCommand)
        actualType shouldBe expectedType
    }

    companion object {
        @JvmStatic
        fun commandTypeProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("A1 1", CliCommandType.PLACE),
                Arguments.of("A1 clear", CliCommandType.CLEAR),
                Arguments.of("hint", CliCommandType.HINT),
                Arguments.of("check", CliCommandType.CHECK),
                Arguments.of("exit", CliCommandType.EXIT),
                Arguments.of("unknown command fallback", CliCommandType.PLACE),
            )
    }
}