package com.brzozowski

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Aleksander Brzozowski
 */
@SpringBootApplication
class BookStoreApplication {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(BookStoreApplication::class.java, *args)
        }
    }
}