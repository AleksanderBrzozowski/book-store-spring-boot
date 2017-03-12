package com.brzozowski.util

/**
 * @author Aleksander Brzozowski
 */

inline fun <T> T.ifPresent(block: T.() -> Unit): T {
    if (this != null) {
        block()
    }
    return this
}