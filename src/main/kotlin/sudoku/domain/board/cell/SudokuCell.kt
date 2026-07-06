package sudoku.domain.board.cell

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.Objects

class SudokuCell(
    initialValue: Int,
    val position: CellPosition,
    val answer: Int,
    val isModifiable: Boolean,
) {
    val value: Int
        get() = _value

    private var _value = initialValue

    fun setValue(incomingValue: Int): Either<String, String> =
        if (!isModifiable) {
            MODIFY_PREFILLED_ERROR_MSG.format(position.referenceLabel).left()
        } else if (incomingValue == _value) {
            REPEATED_MOVE_ERROR_MSG.left()
        } else {
            _value = incomingValue
            MOVE_ACCEPTED_SUCCESS_MSG.right()
        }

    fun clearValue(): Either<String, String> =
        if (!isFilled()) {
            CLEAR_EMPTY_CELL_ERROR_MSG.format(position.referenceLabel).left()
        } else if (!isModifiable) {
            MODIFY_PREFILLED_ERROR_MSG.format(position.referenceLabel).left()
        } else {
            _value = EMPTY_CELL_VALUE
            MOVE_ACCEPTED_SUCCESS_MSG.right()
        }

    fun isFilled() = _value != EMPTY_CELL_VALUE

    fun isCorrectlyFilled() = _value == answer

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SudokuCell) return false

        if (answer != other.answer) return false
        if (isModifiable != other.isModifiable) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int =
        Objects.hash(answer, isModifiable, position)


    companion object {
        const val EMPTY_CELL_VALUE = 0
        private const val MOVE_ACCEPTED_SUCCESS_MSG = "Move accepted."
        private const val MODIFY_PREFILLED_ERROR_MSG = "Invalid move. %s is prefilled."
        private const val REPEATED_MOVE_ERROR_MSG = "Move value is same as current cell value. Please try another cell."
        private const val CLEAR_EMPTY_CELL_ERROR_MSG = "Invalid move. Cannot clear empty cell."
    }
}