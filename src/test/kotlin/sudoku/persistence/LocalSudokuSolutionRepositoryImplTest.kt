package sudoku.persistence

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class LocalSudokuSolutionRepositoryImplTest {


    @Test
    fun `should return random solution sudoku board`() {
        val repository = LocalSudokuSolutionRepositoryImpl()

        val firstSolution = repository.getRandomSolution()

        var hasSameSolution = true
        var remainingAttempts = 10
        while (remainingAttempts > 0) {
            val secondSolution = repository.getRandomSolution()
            if (firstSolution != secondSolution) {
                hasSameSolution = false
                break
            }
            remainingAttempts--
        }

        hasSameSolution shouldBe false
    }

    @Test
    fun `should throw exception when solution file does not exists`() {
        val exception =
            shouldThrow<IllegalStateException> {
                LocalSudokuSolutionRepositoryImpl("/wrong-file-path.json")
            }

        exception.message shouldContain "file not found"
    }

    @Test
    fun `should throw exception when no solutions are seeded`() {
        val exception =
            shouldThrow<IllegalStateException> {
                LocalSudokuSolutionRepositoryImpl("/empty_sudoku_solutions.json")
            }

        exception.message shouldContain "No Sudoku solutions found"
    }
}