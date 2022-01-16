package com.assignment.cloudstorage.controllers

import com.assignment.cloudstorage.entities.FileEntity
import com.assignment.cloudstorage.entities.UserEntity
import com.assignment.cloudstorage.services.AuthenticationService
import com.assignment.cloudstorage.services.FileService
import com.assignment.cloudstorage.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var authenticationService: AuthenticationService
    @Autowired
    lateinit var fileService: FileService

    @PostMapping("/signup")
    fun signup(userEntity: UserEntity): ResponseEntity<UserEntity> {

        if (!userService.isUsernameAvailable(userEntity.username)) {
            userService.createUser(userEntity)
            return ResponseEntity(HttpStatus.CREATED)
        }

        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @GetMapping("/{userName}")
    fun getUser(@CookieValue(value = "username") username: String ,
                @CookieValue(value = "password") password: String ,
                @PathVariable("userName") userName: String): ResponseEntity<UserEntity>
    {
        authenticationService.authenticate(username, password)?.let {
            return ResponseEntity(it, HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/signin")
    fun signIn(userEntity: UserEntity): ResponseEntity<UserEntity> {
        authenticationService.authenticate(userEntity.username, userEntity.password)?.let {
            return ResponseEntity(HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @GetMapping("/files/{userName}")
    fun getUserFiles(@CookieValue(value = "username") username: String ,
                @CookieValue(value = "password") password: String ,
                @PathVariable("userName") userName: String): ResponseEntity<List<FileEntity>> {
        authenticationService.authenticate(username, password)?.let {
            return ResponseEntity(fileService.provideFiles(it.username), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

}