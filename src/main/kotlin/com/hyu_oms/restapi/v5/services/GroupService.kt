package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.GroupListItemDto
import com.hyu_oms.restapi.v5.dtos.GroupListResponseDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
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
}