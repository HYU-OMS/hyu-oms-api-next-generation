package com.hyu_oms.restapi.v5.user

import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {
  fun addNewOne(name: String): User {
    val newUser = User(name = name)
    this.userRepository.save(newUser)

    return newUser
  }
}