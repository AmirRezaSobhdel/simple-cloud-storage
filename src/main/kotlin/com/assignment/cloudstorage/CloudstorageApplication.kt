package com.assignment.cloudstorage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CloudstorageApplication

fun main(args: Array<String>) {
	runApplication<CloudstorageApplication>(*args)
}
