package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.group.GroupAddResponseDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListResponseDto
import com.hyu_oms.restapi.v5.dtos.group.GroupUpdateResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.GroupAlreadyCreatedIn12HoursException
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
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

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
  }

  private fun getMembersByUser(): List<Member> {
    val user = this.getUserFromContext()
    return this.memberRepository.findAllByUserAndEnabledIsTrue(user)
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
    val user = this.getUserFromContext()

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

  @Transactional(readOnly = false)
  fun updateGroup(groupId: Long, name: String?, allowRegister: Boolean?): GroupUpdateResponseDto {
    val user = this.getUserFromContext()

    val targetGroup = this.groupRepository.findByIdOrNull(id = groupId) ?: throw GroupNotFoundException()
    if(targetGroup.creator.id != user.id) {
      throw PermissionDeniedException()
    }

    if(name != null) {
      targetGroup.name = name
    }

    if(allowRegister != null) {
      targetGroup.allowRegister = allowRegister
    }

    this.groupRepository.save(targetGroup)

    return GroupUpdateResponseDto(groupId = groupId)
  }
}