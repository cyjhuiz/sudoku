package sudoku.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import sudoku.domain.RawSudokuBoard
import com.fasterxml.jackson.module.kotlin.readValue
import sudoku.domain.repository.SudokuSolutionRepository

class LocalSudokuSolutionRepositoryImpl(
    jsonFilePath: String = "/sudoku_solutions.json",
): SudokuSolutionRepository {
    private val solutions: List<RawSudokuBoard>

    private val objectMapper = ObjectMapper()

    init {
        val inputStream = this.javaClass.getResourceAsStream(jsonFilePath)
            ?: throw IllegalStateException("Local Sudoku solution file not found")

        val jsonString = inputStream.bufferedReader().use { it.readText() }

        solutions = objectMapper.readValue<List<RawSudokuBoard>>(jsonString)

        if (solutions.isEmpty()) {
            throw IllegalStateException("Game cannot start. No Sudoku solutions found.")
        }
    }

    override fun getRandomSolution() = solutions.random()
}