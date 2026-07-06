package sudoku.cli

enum class CliCommandType {
    CHECK,
    CLEAR,
    EXIT,
    HINT,
    PLACE,
    ;

    companion object {
        fun from(command: String) =
            when {
                command == EXIT.name.lowercase() -> EXIT
                command == CHECK.name.lowercase() -> CHECK
                command == HINT.name.lowercase() -> HINT
                command.endsWith(CLEAR.name.lowercase()) -> CLEAR
                else -> PLACE
            }
    }
}