package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringRSocketClientExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringRSocketClientExampleApplication>(*args)
}
