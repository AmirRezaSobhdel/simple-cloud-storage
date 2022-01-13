package com.assignment.cloudstorage.services

import com.assignment.cloudstorage.entities.UserEntity
import com.assignment.cloudstorage.repos.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.security.Principal
import java.security.SecureRandom
import java.util.*

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var hashService: HashService

    fun isUsernameAvailable(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun createUser(user: UserEntity): UserEntity {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        val encodedSalt = Base64.getEncoder().encodeToString(salt)
        val hashedPassword: String = hashService.getHashedValue(user.password, encodedSalt)
        val userEntity = UserEntity(encodedSalt, user.firstName, user.lastName,
                user.username, hashedPassword)
        return userRepository.save(userEntity)
    }


    @Cacheable(value = ["users"], key = "#userName")
    fun getUser(userName: String): UserEntity? {
        return userRepository.getByUsername(userName)
    }

    @CachePut(value = ["users"], key = "#userName")
    fun updateUsedStorage(userName: String, usedStorage: Long): UserEntity? {
        val userEntity = userRepository.getByUsername(userName)!!
        userEntity.usedStorage = usedStorage
        return userRepository.save(userEntity)
    }

}