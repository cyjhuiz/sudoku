package sudoku.domain

import arrow.core.Either
import sudoku.domain.SudokuConstants.NUM_PREFILLED_CELLS
import sudoku.domain.SudokuConstants.SUDOKU_GRID_LENGTH
import sudoku.domain.board.SudokuBoardFactory
import sudoku.domain.hint.HintManager
import sudoku.domain.hint.HintManagerFactory
import sudoku.domain.move.Move
import sudoku.domain.violation.ViolationsTracker
import sudoku.domain.violation.ViolationsTrackerFactory

class SudokuGame(
    private val sudokuBoardFactory: SudokuBoardFactory,
    private val hintManagerFactory: HintManagerFactory,
    private val violationsTrackerFactory: ViolationsTrackerFactory
) {
    lateinit var playerBoard: SudokuBoard

    private lateinit var hintManager: HintManager

    private lateinit var violationsTracker: ViolationsTracker

    private var numCorrectlyFilledCells = 0

    var hasMadeFirstMove = false

    init {
        startNewGame()
    }

    fun startNewGame() {
        playerBoard = sudokuBoardFactory.createRandomBoard()
        hintManager = hintManagerFactory.create(playerBoard)
        violationsTracker = violationsTrackerFactory.create(playerBoard)

        hasMadeFirstMove = false
        numCorrectlyFilledCells = 0
    }

    fun place(move: Move): Either<String, String> {
        if (!hasMadeFirstMove) {
            hasMadeFirstMove = true
        }

        val targetCell = playerBoard[move.position.row][move.position.col]
        val valueBeforeUpdate = targetCell.value

        return targetCell.setValue(move.value).onRight {
            if (targetCell.isCorrectlyFilled()) {
                numCorrectlyFilledCells++
            }

            hintManager.trackCellUpdate(targetCell)
            violationsTracker.trackCellUpdate(targetCell, valueBeforeUpdate)
        }
    }

    fun clear(move: Move): Either<String, String> {
        val targetCell = playerBoard[move.position.row][move.position.col]
        val valueBeforeUpdate = targetCell.value
        val wasClearingCorrectValue = targetCell.isCorrectlyFilled()

        return targetCell.clearValue().onRight {
            if (wasClearingCorrectValue) {
                numCorrectlyFilledCells--
            }

            hintManager.trackCellUpdate(targetCell)
            violationsTracker.trackCellUpdate(targetCell, valueBeforeUpdate)
        }
    }

    fun showHint() = hintManager.showHint()

    fun checkViolationResult() = violationsTracker.checkViolationResult()

    fun hasCompleted() = numCorrectlyFilledCells == NUM_STARTING_PLAYER_CELLS

    private companion object {
        const val TOTAL_CELLS = SUDOKU_GRID_LENGTH * SUDOKU_GRID_LENGTH
        const val NUM_STARTING_PLAYER_CELLS = TOTAL_CELLS - NUM_PREFILLED_CELLS
    }
}