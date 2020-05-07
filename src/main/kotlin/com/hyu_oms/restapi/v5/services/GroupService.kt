package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.GroupListResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

  private fun getMembers(): List<Member> {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    val user = this.userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

    return memberRepository.findAllByUser(user)
  }

  private fun generateGroupListResponseDto(pages: Page<Group>): GroupListResponseDto {
    return GroupListResponseDto(
        contents = pages.stream()
            .map { group: Group ->
              this.modelMapper.map(
                  group,
                  GroupListItemDto::class.javaObjectType
              )
            }
            .collect(Collectors.toList()),
        totalPages = pages.totalPages,
        totalElements = pages.totalElements
    )
  }

  fun getEnrolledList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())
    val members = this.getMembers()
    val pages = this.groupRepository.findAllEnrolled(members, pageRequest)

    return this.generateGroupListResponseDto(pages)
  }

  fun getNotEnrolledAndRegisterAllowedList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())
    val members = this.getMembers()
    val pages = this.groupRepository.findAllNotEnrolledAndRegisterAllowed(members, pageRequest)

    return this.generateGroupListResponseDto(pages)
  }
}