package com.brzozowski

import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions

/**
 * @author Aleksander Brzozowski
 */



class TestExtensions{
    companion object {
        inline fun <reified T> assertThrownBy(crossinline block: () -> Unit): AbstractThrowableAssert<*, *>? {
            return Assertions.assertThatThrownBy { block.invoke() }
                    .isInstanceOf(T::class.java)
        }
    }
}