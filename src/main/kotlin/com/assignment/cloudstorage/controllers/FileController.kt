package com.assignment.cloudstorage.controllers

import com.assignment.cloudstorage.entities.FileEntity
import com.assignment.cloudstorage.services.AuthenticationService
import com.assignment.cloudstorage.services.FileService
import com.assignment.cloudstorage.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileController {

    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var authenticationService: AuthenticationService
    @Autowired
    lateinit var fileService: FileService

    @PostMapping()
    fun uploadFile(@CookieValue(value = "username") username: String,
                   @CookieValue(value = "password") password: String,
                   @RequestParam("uploadFile") uploadedFile: MultipartFile): ResponseEntity<FileEntity> {
        authenticationService.authenticate(username, password)?.let { user ->

            fileService.provideFile(user.username + "-" + uploadedFile.originalFilename)?.let {
                // the file with this name already exists
                return ResponseEntity(HttpStatus.CONFLICT)
            }

            fileService.uploadFile(uploadedFile, user.username)?.let {

                userService.updateUsedStorage(user.username, user.usedStorage + uploadedFile.size)

                return ResponseEntity(HttpStatus.CREATED)
            }
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(@CookieValue(value = "username") username: String,
                   @CookieValue(value = "password") password: String,
                   @PathVariable("fileId") fileId: String): ResponseEntity<FileEntity>
    {
        authenticationService.authenticate(username, password)?.let { user ->

            fileService.provideFile(fileId)?.let {
                userService.updateUsedStorage(user.username,
                        user.usedStorage - it.fileSize)
                fileService.removeFile(fileId)
                return ResponseEntity(HttpStatus.OK)
            }
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    @GetMapping("/{fileId}")
    fun getFile(@CookieValue(value = "username") username: String,
                   @CookieValue(value = "password") password: String,
                   @PathVariable("fileId") fileId: String): ResponseEntity<FileEntity>
    {
        authenticationService.authenticate(username, password)?.let { user ->

            val file = fileService.provideFile(fileId)
            println(file)

            return ResponseEntity(file, HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

}