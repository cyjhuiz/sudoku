# Sudoku

This is a Cli Sudoku application built with Java 21.

The project follows Domain-Driven Design, where domain models encapsulate business logic. This makes it easier to extend and understand.

## Architecture

The application follows a layered architecture:

CLI (SudokuCli) -> Application (SudokuApp) -> Repository (SudokuBoardRepository)

Each layer has a single responsibility:
- CLI layer - Handles user interaction, command parsing, and input validation.
- Application layer - Coordinates domain operations without being coupled to the CLI.
- Repository layer - Retrieves Sudoku boards.

## CLI Layer

The CLI uses `CliCommandExecutor` for command execution instead of a large chain of if/else statements. This makes adding new commands straightforward while keeping command execution easy to follow.

User input is validated by `CliMoveParser` before reaching the application layer. By validating inputs early, the business logic only needs to handle valid and well-formed requests.

## Application Layer

### Sudoku App

`SudokuApp` acts as the application's orchestration layer and is decoupled from the CLI as it returns domain objects rather than presentation specific responses.

This makes it possible to reuse the same application logic from another interface, such as a REST API without much modification.

To avoid becoming a "god class", responsibilities are delegated to specialised domain components such as:
- `SudokuBoardFactory`
- `HintManager`
- `ViolationTracker`

### HintManager

`HintManager` manages which cells are eligible to be returned as hints.

It maintains correctly and incorrectly filled positions using HashSets. Since cells frequently move between these states, O(1) insertion and removal provides efficient updates.

This is a better alternative than using bruteforce approach to iterate through the entire board till an eligible hint is found.

### ViolationTracker

`ViolationTracker` keeps track of value frequencies within each row, column, and subgrid using a HashMap.

Whenever a value appears more than once within one of these groups, it is reported as a violation.

For subgrid violations, the affected subgrid is identified using its top-left coordinate(e.g. A1) for user-friendliness.

Overall, tracking violations using HashMap using to do one pass to get the violation result. This is a better alternative than using bruteforce approach to compare each cell with all other cells which results in multiple passes.

### SudokuBoardFactory

`SudokuBoardFactory` generates player boards by randomising the pre-filled cells while maintaining an even distribution across the puzzle.
This makes the game much more balanced as pre-filled cells do not crowd around a certain place.

### Error Handling
This application uses ArrowKt to represent success/failure instead of throwing exceptions as exceptions can be expensive.
ArrowKt uses Right to represent success and Left to represent failure, so a typical return value will look like Either<ErrorCode, Unit>.

### TypeAlias
TypeAlias are present for readability while having native type support (e.g. board.map{} ):
- `List<List<SudokuCell>>` -> `SudokuBoard`
- `List<List<Int>>` -> `RawSudokuBoard`
- `Pair<Int, Int>` -> `Subgrid`

### Testing

#### Unit Tests

Most business logic is covered by unit tests to verify extensive behaviour and scenarios quickly.

#### Integration Tests

Since integration tests are slower, they will focus on main user workflows such as placing and clearing values.

#### Test Builders

Test Builders are used to create test objects with common defaults. This helps to avoid duplication of keying in parameters, making tests easy to write.

### Libraries
- Arrow - Functional error handling using Either
- JUnit - Test framework
- Kotest - Fluent assertions
- MockK - Mocking framework for Kotlin
- JaCoCo - Test coverage reporting