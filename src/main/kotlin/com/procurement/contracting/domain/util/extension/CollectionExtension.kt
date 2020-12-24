package com.procurement.contracting.domain.util.extension

import com.procurement.contracting.lib.functional.Option
import com.procurement.contracting.lib.functional.Result

fun <T, R, E> List<T>?.mapOptionalResult(block: (T) -> Result<R, E>): Result<Option<List<R>>, E> {
    if (this == null)
        return Result.success(Option.none())

    val r = mutableListOf<R>()
    for (element in this) {
        when (val result = block(element)) {
            is Result.Success -> r.add(result.value)
            is Result.Failure -> return result
        }
    }
    return Result.success(Option.pure(r))
}

fun <T, R, E> List<T>.mapResult(block: (T) -> Result<R, E>): Result<List<R>, E> {
    val r = mutableListOf<R>()
    for (element in this) {
        when (val result = block(element)) {
            is Result.Success -> r.add(result.value)
            is Result.Failure -> return result
        }
    }
    return Result.success(r)
}

fun <T, R, E> List<T>.mapResultPair(block: (T) -> Result<R, E>): Result<List<R>, FailPair<E, T>> {
    val r = mutableListOf<R>()
    for (element in this) {
        when (val result = block(element)) {
            is Result.Success -> r.add(result.value)
            is Result.Failure -> return Result.failure(FailPair(result.reason, element))
        }
    }
    return Result.success(r)
}
data class FailPair<out E, out T> constructor(val fail: E, val element: T)

fun <T> T?.toListOrEmpty(): List<T> = if (this != null) listOf(this) else emptyList()

inline fun <T, V> Collection<T>.toSetBy(selector: (T) -> V): Set<V> {
    val collections = LinkedHashSet<V>()
    forEach {
        collections.add(selector(it))
    }
    return collections
}

fun <T> getNewElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    received.asSet().subtract(known.asSet())

fun <T> getElementsForUpdate(received: Set<T>, known: Set<T>) = known.intersect(received)

private fun <T> Iterable<T>.asSet(): Set<T> = when (this) {
    is Set -> this
    else -> this.toSet()
}

inline fun <T, V> Collection<T>?.getDuplicate(selector: (T) -> V): T? {
    val unique = HashSet<V>()
    this?.forEach { item ->
        if (!unique.add(selector(item))) return item
    }
    return null
}
