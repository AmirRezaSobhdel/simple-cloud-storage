package com.assignment.cloudstorage.controllers

import com.assignment.cloudstorage.entities.FileEntity
import com.assignment.cloudstorage.services.AuthenticationService
import com.assignment.cloudstorage.services.FileService
import com.assignment.cloudstorage.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths
import java.security.Principal

@CrossOrigin
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

//    @GetMapping("/{fileId}")
//    fun getFile(@CookieValue(value = "username") username: String,
//                   @CookieValue(value = "password") password: String,
//                   @PathVariable("fileId") fileId: String): ResponseEntity<FileEntity>
//    {
//        authenticationService.authenticate(username, password)?.let { user ->
//
//            val file = fileService.provideFile(fileId)
//            println(file)
//
//            return ResponseEntity(file, HttpStatus.OK)
//        }
//        return ResponseEntity(HttpStatus.UNAUTHORIZED)
//    }


    @GetMapping("{fileId}")
    fun downloadFile(@PathVariable fileId: String,
                     @RequestHeader(value = "Range", required = false) rangeHeader: String?,
                     @CookieValue(value = "username") username: String,
                     @CookieValue(value = "password") password: String
    ): ResponseEntity<StreamingResponseBody?>? {
        authenticationService.authenticate(username, password)?.let { user ->

            val fileEntity = fileService.provideFile(fileId)
            return try {
                val responseStream: StreamingResponseBody
                val filePathString = fileEntity!!.getFilePath()
                val filePath = Paths.get(filePathString)
                val fileSize = Files.size(filePath)
                val buffer = ByteArray(1024)
                val responseHeaders = HttpHeaders()
                if (rangeHeader == null) {
                    responseHeaders.add("Content-Type", fileEntity.getContentType())
                    responseHeaders.add("Content-Length", fileSize.toString())
                    responseStream = StreamingResponseBody { os: OutputStream ->
                        val file = RandomAccessFile(filePathString, "r")
                        try {
                            file.use {
                                var pos: Long = 0
                                file.seek(pos)
                                while (pos < fileSize - 1) {
                                    file.read(buffer)
                                    os.write(buffer)
                                    pos += buffer.size.toLong()
                                }
                                os.flush()
                            }
                        } catch (e: Exception) {
                        }
                    }
                    return ResponseEntity(responseStream, responseHeaders, HttpStatus.OK)
                }
                val ranges = rangeHeader.split("-".toRegex()).toTypedArray()
                val rangeStart = ranges[0].substring(6).toLong()
                var rangeEnd: Long
                rangeEnd = if (ranges.size > 1) {
                    ranges[1].toLong()
                } else {
                    fileSize - 1
                }
                if (fileSize < rangeEnd) {
                    rangeEnd = fileSize - 1
                }
                val contentLength = (rangeEnd - rangeStart + 1).toString()
                responseHeaders.add("Content-Type", fileEntity.getContentType())
                responseHeaders.add("Content-Length", contentLength)
                responseHeaders.add("Accept-Ranges", "bytes")
                responseHeaders.add("Content-Range", "bytes $rangeStart-$rangeEnd/$fileSize")
                val _rangeEnd = rangeEnd
                responseStream = StreamingResponseBody { os: OutputStream ->
                    val file = RandomAccessFile(filePathString, "r")
                    try {
                        file.use {
                            var pos = rangeStart
                            file.seek(pos)
                            while (pos < _rangeEnd) {
                                file.read(buffer)
                                os.write(buffer)
                                pos += buffer.size.toLong()
                            }
                            os.flush()
                        }
                    } catch (e: Exception) {
                    }
                }
                ResponseEntity(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT)
            } catch (e: FileNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } catch (e: IOException) {
                ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

}