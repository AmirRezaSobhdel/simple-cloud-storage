package com.assignment.cloudstorage.repos

import com.assignment.cloudstorage.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface UserRepository: JpaRepository<UserEntity, String> {

    fun existsByUsername(userName: String): Boolean
    fun getByUsername(userName: String): UserEntity?

}