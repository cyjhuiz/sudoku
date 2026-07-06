package sudoku.cli.util

import kotlin.system.exitProcess

// This function allows mocked calls for testability without really shutting down the JVM
fun systemExit(code:Int) {
    exitProcess(code)
}