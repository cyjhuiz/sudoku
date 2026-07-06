package sudoku.domain.violation

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import sudoku.domain.Subgrid
import sudoku.domain.SudokuBoard
import sudoku.domain.board.cell.CellPosition
import sudoku.domain.board.cell.SudokuCell
import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE
import sudoku.util.getOrPutHashMap
import sudoku.util.toFullCodeIndexFromSubgrid
import sudoku.util.toUserIndex
import sudoku.util.toUserRowChar

class ViolationsTracker(
    playerBoard: SudokuBoard
) {
    private val row2ValueFrequency = mutableMapOf<Int, HashMap<Int, Int>>()

    private val col2ValueFrequency = mutableMapOf<Int, HashMap<Int, Int>>()

    private val subgrid2ValueFrequency = mutableMapOf<Subgrid, HashMap<Int, Int>>()

    init {
        playerBoard.forEach { row ->
            row.forEach { cell ->
                if (cell.isFilled()) {
                    val rowFrequency = row2ValueFrequency.getOrPutHashMap(cell.position.row)
                    rowFrequency[cell.value] = 1

                    val colFrequency = col2ValueFrequency.getOrPutHashMap(cell.position.col)
                    colFrequency[cell.value] = 1

                    val subGridFrequency = subgrid2ValueFrequency.getOrPutHashMap(cell.position.subgrid)
                    subGridFrequency[cell.value] = 1
                }
            }
        }
    }

    fun checkViolationResult(): Either<List<String>, String> {
        val violations = mutableListOf<String>()
        row2ValueFrequency.entries.forEach { (row, valueFrequency) ->
            valueFrequency.forEach { (value, frequency) ->
                if (hasViolations(frequency)) {
                    violations.add("Number $value already exists in Row ${row.toUserRowChar()}.")
                }
            }
        }

        col2ValueFrequency.entries.forEach { (col, valueFrequency) ->
            valueFrequency.forEach { (value, frequency) ->
                if (hasViolations(frequency)) {
                    violations.add("Number $value already exists in Column ${col.toUserIndex()}.")
                }
            }
        }

        subgrid2ValueFrequency.entries.forEach { (subgrid, valueFrequency) ->
            valueFrequency.forEach { (value, frequency) ->
                if (hasViolations(frequency)) {
                    violations.add("Number $value already exists in the same 3x3 subgrid ${subgridReferenceLabel(subgrid)}.")
                }
            }
        }

        return if (violations.isEmpty()) {
            "No violations detected.".right()
        } else {
            violations.left()
        }
    }

    fun trackCellUpdate(cell: SudokuCell, valueBeforeUpdate: Int) {
        decrementValueCount(valueBeforeUpdate, cell.position)
        incrementValueCount(cell.value, cell.position)
    }

    private fun incrementValueCount(value: Int, position: CellPosition) {
        if (value != EMPTY_CELL_VALUE) {
            val rowValueFrequency = row2ValueFrequency.getOrPutHashMap(position.row)
            rowValueFrequency.merge(value, 1, Int::plus)

            val colValueFrequency = col2ValueFrequency.getOrPutHashMap(position.col)
            colValueFrequency.merge(value, 1, Int::plus)

            val subGridValueFrequency = subgrid2ValueFrequency.getOrPutHashMap(position.subgrid)
            subGridValueFrequency.merge(value, 1, Int::plus)
        }
    }

    private fun decrementValueCount(value: Int, position: CellPosition) {
        if (value != EMPTY_CELL_VALUE) {
            val rowValueFrequency = row2ValueFrequency.getValue(position.row)
            rowValueFrequency.merge(value, 1, Int::minus)

            val colValueFrequency = col2ValueFrequency.getValue(position.col)
            colValueFrequency.merge(value, 1, Int::minus)

            val subGridValueFrequency = subgrid2ValueFrequency.getValue(position.subgrid)
            subGridValueFrequency.merge(value, 1, Int::minus)
        }
    }

    private fun hasViolations(frequency: Int) = frequency > 1

    private fun subgridReferenceLabel(subgrid: Subgrid): String {
        val (row, col) = subgrid
        val subgridFullRowIdx = row.toFullCodeIndexFromSubgrid()

        return "${subgridFullRowIdx.toUserRowChar()}${col.toUserIndex()}"
    }
}