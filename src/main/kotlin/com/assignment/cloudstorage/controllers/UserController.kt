package com.assignment.cloudstorage.controllers

import com.assignment.cloudstorage.entities.UserEntity
import com.assignment.cloudstorage.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping
    fun signup(userEntity: UserEntity): ResponseEntity<UserEntity> {

        if (!userService.isUsernameAvailable(userEntity.username)) {
            userService.createUser(userEntity)
            return ResponseEntity(HttpStatus.CREATED)
        }

        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @GetMapping("/{userName}")
    fun getUser(@PathVariable("userName") userName: String)
    {
        userService.getUser(userName)
    }

}