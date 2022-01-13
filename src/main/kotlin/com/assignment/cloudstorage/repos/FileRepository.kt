package com.assignment.cloudstorage.repos

import com.assignment.cloudstorage.entities.FileEntity
import com.assignment.cloudstorage.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface FileRepository: JpaRepository<FileEntity, String> {

    fun existsByUsername(username: String): Boolean
    fun getUserFileByFileIdAndUsername(fileId: String, username: String): FileEntity
    fun getAllByUsername(username: String): List<FileEntity>
    fun deleteByFileId(fileId: String)

}