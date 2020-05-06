package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.GroupListResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val userRepository: UserRepository
) {
  val modelMapper: ModelMapper = ModelMapper()

  fun getEnrolledList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size)
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()

    val user = this.userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
    val members = this.memberRepository.findAllByUser(user)

    val groupPages = this.groupRepository.findDistinctByEnabledIsTrueAndMembersInOrderByIdAsc(members, pageRequest)
    val groupCount = this.groupRepository.countDistinctByEnabledIsTrueAndMembersIn(members)

    val groupList = groupPages.stream()
        .map { group: Group ->
          this.modelMapper.map(
              group,
              GroupListItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())

    return GroupListResponseDto(list = groupList, count = groupCount)
  }

  fun getNotEnrolledAndRegisterAllowedList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size)
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()

    val user = this.userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
    val members = this.memberRepository.findAllByUser(user)

    val groupPages = this.groupRepository.findDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotInOrderByIdAsc(members, pageRequest)
    val groupCount = this.groupRepository.countDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotIn(members)

    val groupList = groupPages.stream()
        .map { group: Group ->
          this.modelMapper.map(
              group,
              GroupListItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())

    return GroupListResponseDto(list = groupList, count = groupCount)
  }
}