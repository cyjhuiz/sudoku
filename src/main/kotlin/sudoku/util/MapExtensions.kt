package sudoku.util

// This is used to simplify null handling when we know the key will have a value
inline fun <reified T> Map<String, *>.getOrThrow(key: String): T {
    val value = this[key]
        ?: throw NoSuchElementException("Required key '$key' was not found.")

    return value as? T
        ?: throw IllegalStateException(
            "Value for key '$key' is of type ${value::class.qualifiedName}, " +
                    "expected ${T::class.qualifiedName}."
        )
}

// Same as getOrPut but defaults with empty hashmap
fun <K1, K2, V> MutableMap<K1, HashMap<K2, V>>.getOrPutHashMap(
    key: K1
): HashMap<K2, V> = getOrPut(key) { hashMapOf() }