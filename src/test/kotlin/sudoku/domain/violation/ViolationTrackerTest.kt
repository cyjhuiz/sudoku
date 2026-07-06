package sudoku.domain.violation

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import sudoku.domain.board.SudokuPlayerBoardTestBuilder
import sudoku.domain.board.cell.SudokuCell.Companion.EMPTY_CELL_VALUE
import sudoku.domain.SudokuBoard

class ViolationTrackerTest {
    @Test
    fun `should track row violation`() {
        val emptyPlayerBoard =
            SudokuPlayerBoardTestBuilder()
                .build(isFullyModifiableBoard = true)

        val violationTracker = ViolationsTracker(emptyPlayerBoard)

        setAndTrackDuplicateValues(
            positions = conflictingRowPositions,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )

        val result = violationTracker.checkViolationResult().shouldBeLeft()

        result shouldHaveSize 1
        result.first() shouldBe ROW_VIOLATION_MESSAGE
    }

    @Test
    fun `should track column violation`() {
        val emptyPlayerBoard =
            SudokuPlayerBoardTestBuilder()
                .build(isFullyModifiableBoard = true)

        val violationTracker = ViolationsTracker(emptyPlayerBoard)

        setAndTrackDuplicateValues(
            positions = conflictingColumnPositions,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )

        val result = violationTracker.checkViolationResult().shouldBeLeft()

        result shouldHaveSize 1
        result.first() shouldBe COL_VIOLATION_MESSAGE
    }

    @Test
    fun `should track subgrid violation`() {
        val emptyPlayerBoard =
            SudokuPlayerBoardTestBuilder()
                .build(isFullyModifiableBoard = true)

        val violationTracker = ViolationsTracker(emptyPlayerBoard)

        setAndTrackDuplicateValues(
            positions = conflictingSubgridPositions,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )

        val result = violationTracker.checkViolationResult().shouldBeLeft()

        result shouldHaveSize 1
        result.first() shouldBe SUBGRID_VIOLATION_MESSAGE
    }

    @Test
    fun `should track multiple types of violations at once`() {
        val emptyPlayerBoard =
            SudokuPlayerBoardTestBuilder()
                .build(isFullyModifiableBoard = true)

        val violationTracker = ViolationsTracker(emptyPlayerBoard)

        setAndTrackDuplicateValues(
            positions = allConflictingPositions,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )

        val result = violationTracker.checkViolationResult().shouldBeLeft()

        result shouldHaveSize 3
        result shouldContainAllInAnyOrder listOf(
            ROW_VIOLATION_MESSAGE,
            COL_VIOLATION_MESSAGE,
            SUBGRID_VIOLATION_MESSAGE,
        )
    }

    @Test
    fun `should remove violation when previous cell violation resolves`() {
        val emptyPlayerBoard =
            SudokuPlayerBoardTestBuilder()
                .build(isFullyModifiableBoard = true)

        val violationTracker = ViolationsTracker(emptyPlayerBoard)

        setAndTrackDuplicateValues(
            positions = conflictingRowPositions,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )

        val initialResult = violationTracker.checkViolationResult().shouldBeLeft()

        initialResult shouldHaveSize 1

        val otherConflictingPosition = conflictingRowPositions.first()
        setAndTrackCell(
            position = otherConflictingPosition,
            value = EMPTY_CELL_VALUE,
            playerBoard = emptyPlayerBoard,
            violationsTracker = violationTracker
        )


        val finalResult = violationTracker.checkViolationResult().shouldBeRight()
        finalResult shouldBe "No violations detected."
    }

    private fun setAndTrackDuplicateValues(
        positions: Set<Pair<Int, Int>>,
        playerBoard: SudokuBoard,
        violationsTracker: ViolationsTracker
    ) {
        positions.forEach { position ->
            setAndTrackCell(
                position = position,
                value = VALUE,
                playerBoard = playerBoard,
                violationsTracker = violationsTracker,
            )
        }
    }

    private fun setAndTrackCell(
        position: Pair<Int, Int>,
        value: Int,
        playerBoard: SudokuBoard,
        violationsTracker: ViolationsTracker
    ) {
        val (row, col) = position
        val cell = playerBoard[row][col]
        val valueBeforeUpdate = cell.value
        cell.setValue(value)
        violationsTracker.trackCellUpdate(cell, valueBeforeUpdate)
    }

    private companion object {
        const val VALUE = 1

        val cellA1Position = (0 to 0)
        val cellA9Position = (0 to 8)
        val cellB2Position = (1 to 1)
        val cellI1Position = (8 to 0)

        val conflictingRowPositions = setOf(cellA1Position, cellA9Position)
        val conflictingColumnPositions = setOf(cellA1Position, cellI1Position)
        val conflictingSubgridPositions = setOf(cellA1Position, cellB2Position)
        val allConflictingPositions =
            conflictingRowPositions +
                    conflictingColumnPositions +
                    conflictingSubgridPositions


        const val ROW_VIOLATION_MESSAGE = "Number $VALUE already exists in Row A."
        const val COL_VIOLATION_MESSAGE = "Number $VALUE already exists in Column 1."
        const val SUBGRID_VIOLATION_MESSAGE = "Number $VALUE already exists in the same 3x3 subgrid A1."
    }
}