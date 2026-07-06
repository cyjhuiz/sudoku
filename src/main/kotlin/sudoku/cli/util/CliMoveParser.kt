package sudoku.cli.util

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import sudoku.cli.CliCommandType
import sudoku.domain.SudokuConstants
import sudoku.domain.board.cell.CellPosition
import sudoku.domain.board.cell.SudokuCell
import sudoku.domain.move.Move
import sudoku.util.toCodeIndex
import sudoku.util.toUserRowChar

object CliMoveParser {
    private const val NUM_EXPECTED_MOVE_ARGUMENTS = 2

    private const val COL_MIN_NUMBER = 1
    private const val COL_MAX_NUMBER = SudokuConstants.SUDOKU_GRID_LENGTH

    private const val ROW_MIN_CHAR = 'A'
    private val ROW_MAX_CHAR = SudokuConstants.SUDOKU_GRID_LENGTH.toCodeIndex().toUserRowChar()

    private const val VALUE_MIN_NUMBER = 1
    private const val VALUE_MAX_NUMBER = SudokuConstants.SUDOKU_GRID_LENGTH


    fun parse(inputMoveArgument: String): Either<String, Move> {
        val moveArguments = inputMoveArgument.split(" ")
        if (moveArguments.size != NUM_EXPECTED_MOVE_ARGUMENTS) {
            return "Invalid move. A move should only have $NUM_EXPECTED_MOVE_ARGUMENTS arguments.".left()
        }

        val positionArgument = moveArguments[0]
        val rowIdx =
            parseRowIdx(positionArgument).getOrElse { errorMessage ->
                return errorMessage.left()
            }

        val colIdx =
            parseColIdx(positionArgument).getOrElse { errorMessage ->
                return errorMessage.left()
            }

        val otherArgument = moveArguments[1]
        val value =
            parseValue(otherArgument).getOrElse { errorMessage ->
                return errorMessage.left()
            }


        return Move(
            position =
                CellPosition(
                    row = rowIdx,
                    col = colIdx
                ),
            value = value
        ).right()
    }

    private fun parseRowIdx(positionArgument: String): Either<String, Int> {
        // convert row argument to uppercase for user-friendliness and case insensitivity
        val rowChar = positionArgument[0].uppercaseChar()
        if (rowChar !in ROW_MIN_CHAR..ROW_MAX_CHAR) {
            return "Invalid move. Row character should be between '$ROW_MIN_CHAR' and '$ROW_MAX_CHAR'".left()
        }

        return rowChar.toCodeIndex().right()
    }

    private fun parseColIdx(positionArgument: String): Either<String, Int> {
        val colNumber = positionArgument.substring(1).toIntOrNull()
        if (colNumber == null || colNumber !in COL_MIN_NUMBER .. COL_MAX_NUMBER) {
            return "Invalid move. Column number should be a number between $COL_MIN_NUMBER and $COL_MAX_NUMBER.".left()
        }

        return colNumber.toCodeIndex().right()
    }

    private fun parseValue(argument: String): Either<String, Int> {
        if (argument == CliCommandType.CLEAR.name.lowercase()) {
            return SudokuCell.EMPTY_CELL_VALUE.right()
        }

        val value = argument.toIntOrNull()
        if (value == null || value !in VALUE_MIN_NUMBER .. VALUE_MAX_NUMBER) {
            return "Invalid move. Cell value should be a number between $VALUE_MIN_NUMBER and $VALUE_MAX_NUMBER ".left()
        }
        return value.right()
    }

}