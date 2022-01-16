package com.assignment.cloudstorage.services

import com.assignment.cloudstorage.entities.FileEntity
import com.assignment.cloudstorage.repos.FileRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Paths
import javax.transaction.Transactional

@Service
@Slf4j
class FileService {

    @Autowired
    lateinit var fileRepository: FileRepository

    fun uploadFile(multipartFile: MultipartFile, username: String): FileEntity? {
        var dbFileEntity: FileEntity? = null
        try {
            val file = File("src/main/resources/files/" + username + "-" + multipartFile.originalFilename)
            file.createNewFile()
            multipartFile.transferTo(Paths.get(file.absolutePath))
            dbFileEntity = FileEntity(multipartFile.originalFilename, file.absolutePath,
                    multipartFile.size,
                    multipartFile.contentType,
                    username)
        } catch (e: Exception) {
            e.printStackTrace()
            // Problem occured while saving data into FileEntity table
        }
        return if (dbFileEntity != null) fileRepository.save(dbFileEntity) else null
    }

    fun provideFiles(username: String): List<FileEntity> {
        val l = fileRepository.getAllByUsername(username)
        val files: MutableList<FileEntity> = ArrayList()
        for (f in l) {
            f.fileId = f.username + "-" + f.fileName
            files.add(f)
        }
        return l
    }

    fun provideFile(fileId: String): FileEntity? {
        return fileRepository.getUserFileByFileId(fileId)
    }

    fun removeFile(fileId: String) {
        fileRepository.deleteByFileId(fileId)
    }
}