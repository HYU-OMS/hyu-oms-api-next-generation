package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.group.GroupAddResponseDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.exceptions.GroupAlreadyCreatedIn12HoursException
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
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors

@Service
class GroupService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
) {
  val modelMapper: ModelMapper = ModelMapper()

  private fun getMembersByUser(): List<Member> {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.memberRepository.findAllByUserIdAndEnabledIsTrue(userId)
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

  @Transactional(readOnly = true)
  fun getEnrolledList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())
    val members = this.getMembersByUser()
    val pages = this.groupRepository.findDistinctByEnabledIsTrueAndMembersIn(members, pageRequest)

    return this.generateGroupListResponseDto(pages)
  }

  // TODO: 더 나은 방법은?
  @Transactional(readOnly = true)
  fun getNotEnrolledAndRegisterAllowedList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())
    val members = this.getMembersByUser()
    val enrolledGroups = this.groupRepository.findDistinctByEnabledIsTrueAndMembersIn(members)

    val pages = this.groupRepository.findDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndIdIsNotIn(
        enrolledGroups.map { it.id },
        pageRequest
    )

    return this.generateGroupListResponseDto(pages)
  }

  @Transactional(readOnly = false)
  fun addNewGroup(name: String): GroupAddResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    val user = this.userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

    val existingGroup = this.groupRepository.findByCreatorAndEnabledIsTrue(creator = user)
    if(existingGroup != null) {
      val createdAt = existingGroup.createdAt
      val currentTime = LocalDateTime.now(ZoneId.of("UTC"))

      if(createdAt.plusHours(12) > currentTime) {
        throw GroupAlreadyCreatedIn12HoursException()
      }
    }

    val newGroup = Group(
        name = name,
        creator = user
    )
    this.groupRepository.save(newGroup)

    val newMember = Member(
        user = user,
        group = newGroup,
        hasAdminPermission = true
    )
    this.memberRepository.save(newMember)

    return GroupAddResponseDto(newGroupId = newGroup.id)
  }
}