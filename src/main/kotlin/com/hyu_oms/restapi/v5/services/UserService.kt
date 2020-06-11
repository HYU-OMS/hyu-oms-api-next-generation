package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
  fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }
}