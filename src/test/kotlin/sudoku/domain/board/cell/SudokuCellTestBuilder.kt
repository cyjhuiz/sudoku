package sudoku.domain.board.cell

class SudokuCellTestBuilder(
    private val initialValue: Int = 1,
    private val row: Int = 0,
    private val col: Int = 0,
    private val answer: Int = 1,
    private val isModifiable: Boolean = true,
) {
    fun build() =
        SudokuCell(
            initialValue = initialValue,
            position = CellPosition(row, col),
            answer = answer,
            isModifiable = isModifiable
        )
}