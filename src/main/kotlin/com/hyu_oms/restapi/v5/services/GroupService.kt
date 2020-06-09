package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.group.GroupAddResponseDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.group.GroupListResponseDto
import com.hyu_oms.restapi.v5.dtos.group.GroupUpdateAndDeleteResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
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
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class GroupService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }

  private fun getGroupAndCheckIfCreator(user: User, groupId: Long): Group {
    val targetGroup = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()
    if (targetGroup.creator.id != user.id) {
      throw PermissionDeniedException()
    }

    return targetGroup
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
    val user = this.getUserFromContext()
    val members = this.memberRepository.findAllByUserAndEnabledIsTrue(user)
    val pages = this.groupRepository.findDistinctByEnabledIsTrueAndMembersIn(members, pageRequest)

    return this.generateGroupListResponseDto(pages)
  }

  // TODO: 더 나은 방법은?
  @Transactional(readOnly = true)
  fun getNotEnrolledAndRegisterAllowedList(page: Int = 0, size: Int = 20): GroupListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())
    val user = this.getUserFromContext()
    val members = this.memberRepository.findAllByUserAndEnabledIsTrue(user)
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

    // TODO: 그룹 생성 시간 제한 방법 도입 필요.

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
  fun updateGroup(groupId: Long, name: String?, allowRegister: Boolean?): GroupUpdateAndDeleteResponseDto {
    val user = this.getUserFromContext()
    val targetGroup = this.getGroupAndCheckIfCreator(user = user, groupId = groupId)

    if (name != null) {
      targetGroup.name = name
    }

    if (allowRegister != null) {
      targetGroup.allowRegister = allowRegister
    }

    this.groupRepository.save(targetGroup)

    return GroupUpdateAndDeleteResponseDto(groupId = groupId)
  }

  @Transactional(readOnly = false)
  fun deleteGroup(groupId: Long): GroupUpdateAndDeleteResponseDto {
    val user = this.getUserFromContext()

    val targetGroup = this.getGroupAndCheckIfCreator(user = user, groupId = groupId)
    this.memberRepository.deleteMembersByGroup(group = targetGroup)

    targetGroup.enabled = false
    this.groupRepository.save(targetGroup)

    return GroupUpdateAndDeleteResponseDto(groupId = groupId)
  }
}