package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.member.MemberListItemDto
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.exceptions.*
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class MemberService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  @Transactional(readOnly = true)
  fun getMembers(userId: Long, groupId: Long): List<MemberListItemDto> {
    val user = this.userRepository.findByIdAndEnabledIsTrue(id = userId) ?: throw UserNotFoundException()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    ) ?: throw PermissionDeniedException()

    val members = group.members
    return members.stream()
        .map { member: Member ->
          this.modelMapper.map(
              member,
              MemberListItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())
  }

  // 유저가 allowRegister 상태인 그룹에 직접 add 요청을 하게 됨.
  // 이 경우 해당 멤버는 최초 enabled false 가 되며, 관리 권한이 있는 사람이 직접 enabled true 로 변경해야 함.
  @Transactional(readOnly = false)
  fun addMember(userId: Long, groupId: Long): Long {
    val user = this.userRepository.findByIdAndEnabledIsTrue(id = userId) ?: throw UserNotFoundException()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    val newMember = Member(
        user = user,
        group = group,
        enabled = false,
        hasAdminPermission = false
    )
    this.memberRepository.save(newMember)

    return newMember.id
  }

  @Transactional(readOnly = false)
  fun updateMember(
      userId: Long,
      memberId: Long,
      enabled: Boolean? = null,
      hasAdminPermission: Boolean? = null
  ) {
    val user = this.userRepository.findByIdAndEnabledIsTrue(id = userId) ?: throw UserNotFoundException()
    val targetMember = this.memberRepository.findByIdOrNull(memberId) ?: throw MemberNotFoundException()
    val group = targetMember.group

    if (targetMember.user == group.creator) {
      throw CreatorModifyRequestedException()
    }

    val memberForAdminCheck = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    )
    if (memberForAdminCheck == null || !memberForAdminCheck.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    if (enabled != null) {
      targetMember.enabled = enabled
    }

    if (hasAdminPermission != null) {
      targetMember.hasAdminPermission = hasAdminPermission
    }

    this.memberRepository.save(targetMember)
  }
}