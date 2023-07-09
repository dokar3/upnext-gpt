package io.upnextgpt.base.util

/**
 * Remove a specific item, returns the item index if removed or null
 * if target item is not found.
 */
inline fun <T> MutableList<T>.remove(predicate: (T) -> Boolean): Int? {
    val itr = iterator()
    var index = 0
    while (itr.hasNext()) {
        if (predicate(itr.next())) {
            itr.remove()
            return index
        }
        index++
    }
    return null
}