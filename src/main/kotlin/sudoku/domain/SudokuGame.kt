package sudoku.domain

import arrow.core.Either
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

    var hasMadeFirstMove = false
        private set


    init {
        startNewGame()
    }

    fun startNewGame() {
        playerBoard = sudokuBoardFactory.createRandomBoard()
        hintManager = hintManagerFactory.create(playerBoard)
        violationsTracker = violationsTrackerFactory.create(playerBoard)

        hasMadeFirstMove = false
    }

    fun place(move: Move): Either<String, String> {
        if (!hasMadeFirstMove) {
            hasMadeFirstMove = true
        }
        val targetCell = playerBoard[move.position.row][move.position.col]
        val valueBeforeUpdate = targetCell.value

        return targetCell.setValue(move.value).onRight {
            hintManager.trackCellUpdate(targetCell)
            violationsTracker.trackCellUpdate(targetCell, valueBeforeUpdate)
        }
    }

    fun clear(move: Move): Either<String, String> {
        val targetCell = playerBoard[move.position.row][move.position.col]
        val valueBeforeUpdate = targetCell.value

        return targetCell.clearValue().onRight {
            hintManager.trackCellUpdate(targetCell)
            violationsTracker.trackCellUpdate(targetCell, valueBeforeUpdate)
        }
    }

    fun showHint() = hintManager.showHint()

    fun checkViolationResult() = violationsTracker.checkViolationResult()

    fun hasCompleted() =
        playerBoard
            .flatten()
            .all { it.isCorrectlyFilled() }

}
