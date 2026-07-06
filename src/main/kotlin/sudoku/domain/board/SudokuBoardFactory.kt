package sudoku.domain.board

import sudoku.domain.SudokuConstants.NUM_PREFILLED_CELLS
import sudoku.domain.SudokuConstants.SUDOKU_GRID_LENGTH
import sudoku.domain.board.cell.CellPosition
import sudoku.domain.board.cell.SudokuCell
import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE
import sudoku.domain.repository.SudokuSolutionRepository
import sudoku.domain.RawSudokuBoard
import sudoku.domain.SudokuBoard

class SudokuBoardFactory(
    private val sudokuSolutionRepository: SudokuSolutionRepository
) {
    fun createRandomBoard(): SudokuBoard {
        val solutionBoard = sudokuSolutionRepository.getRandomSolution()
        val prefilledPositions = generatePrefilledPositions(solutionBoard)
        return solutionBoard.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, answer ->
                val position = CellPosition(rowIdx, colIdx)
                val isModifiable = Pair(rowIdx, colIdx) !in prefilledPositions
                SudokuCell(
                    initialValue = initialValue(answer, isModifiable),
                    position = position,
                    answer = answer,
                    isModifiable = isModifiable
                )
            }
        }
    }

    private fun generatePrefilledPositions(solutionBoard: RawSudokuBoard): Set<Pair<Int, Int>> {
        val shuffledPositions = shuffledPositions(solutionBoard)

        val prefilledPositions = mutableSetOf<Pair<Int, Int>>()

        addPositionsPerRowEvenly(prefilledPositions, shuffledPositions)
        addRemainingPositions(prefilledPositions, shuffledPositions)

        return prefilledPositions
    }

    private fun addPositionsPerRowEvenly(
        prefilledPositions: MutableSet<Pair<Int, Int>>,
        shuffledPositions: List<Pair<Int, Int>>
    ) {
        val rowCellCount = IntArray(SUDOKU_GRID_LENGTH) { 0 }
        for (position in shuffledPositions) {
            val row = position.first
            if (rowCellCount[row] < MIN_NUM_PREFILLED_PER_ROW) {
                prefilledPositions.add(position)
                rowCellCount[row]++
            }

            if (rowCellCount.all { it == MIN_NUM_PREFILLED_PER_ROW }) break
        }
    }

    private fun addRemainingPositions(
        prefilledPositions: MutableSet<Pair<Int, Int>>,
        shuffledPositions: List<Pair<Int, Int>>
    ) {
        for (position in shuffledPositions) {
            if (prefilledPositions.size == NUM_PREFILLED_CELLS) break

            if (position !in prefilledPositions) {
                prefilledPositions.add(position)
            }
        }
    }

    private fun shuffledPositions(solutionBoard: RawSudokuBoard): List<Pair<Int, Int>> {
        val actualPositions =
            solutionBoard.indices.flatMap { row ->
                solutionBoard[0].indices.map { col ->
                    Pair(row, col)
                }
            }

        return actualPositions.shuffled()
    }

    private fun initialValue(answer: Int, isModifiable: Boolean) =
        if (isModifiable) {
            EMPTY_CELL_VALUE
        } else {
            answer
        }

    private companion object {
        const val MIN_NUM_PREFILLED_PER_ROW = NUM_PREFILLED_CELLS/SUDOKU_GRID_LENGTH
    }
}