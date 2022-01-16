package com.assignment.cloudstorage.services

import com.assignment.cloudstorage.entities.UserEntity
import com.assignment.cloudstorage.repos.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthenticationService {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var hashService: HashService



    fun authenticate(username: String, password: String): UserEntity? {
        val user: UserEntity? = userRepository.getByUsername(username)
        user?.let {
            val encodedSalt: String = user.salt
            val hashedPassword = hashService.getHashedValue(password, encodedSalt)
            if (user.password.equals(hashedPassword)) {
                return user
            }
        }
        return null
    }



}