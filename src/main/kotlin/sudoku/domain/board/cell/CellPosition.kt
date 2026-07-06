package sudoku.domain.board.cell

import sudoku.domain.Subgrid
import sudoku.util.toCodeSubgridIndex
import sudoku.util.toUserIndex
import sudoku.util.toUserRowChar

data class CellPosition(
    val row: Int,
    val col: Int,
) {
    val subgrid: Subgrid = subgrid(row, col)
    val referenceLabel: String = referenceLabel(row, col)

    private fun subgrid(row: Int, col: Int): Subgrid {
        val subgridRow = row.toCodeSubgridIndex()
        val subgridCol = col.toCodeSubgridIndex()

        return Subgrid(subgridRow, subgridCol)
    }

    private fun referenceLabel(row: Int, col: Int): String {
        return "${row.toUserRowChar()}${col.toUserIndex()}"
    }
}