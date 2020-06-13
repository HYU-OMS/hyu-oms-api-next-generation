package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.group.*
import com.hyu_oms.restapi.v5.dtos.member.MemberAddResponseDto
import com.hyu_oms.restapi.v5.dtos.member.MemberListItemDto
import com.hyu_oms.restapi.v5.dtos.member.MemberUpdateAndDeleteResponseDto
import com.hyu_oms.restapi.v5.dtos.member.MemberUpdateRequestDto
import com.hyu_oms.restapi.v5.exceptions.CreatorModifyRequestedException
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.UserNotEnrolledToGroupException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.GroupService
import com.hyu_oms.restapi.v5.services.MemberService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/group")
class GroupAndMemberController(
    private val groupService: GroupService,
    private val memberService: MemberService
) {
  @GetMapping("/enrolled")
  fun getEnrolledGroupList(
      @RequestParam(defaultValue = "0") page: Int,
      @RequestParam(defaultValue = "20") size: Int
  ): GroupListResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.groupService.getEnrolledList(
        userId = userId,
        page = page,
        size = size
    )
  }

  @GetMapping("/not-enrolled")
  fun getNotEnrolledGroupList(
      @RequestParam(defaultValue = "0") page: Int,
      @RequestParam(defaultValue = "20") size: Int
  ): GroupListResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.groupService.getNotEnrolledAndRegisterAllowedList(
        userId = userId,
        page = page,
        size = size
    )
  }

  @GetMapping("/{groupId}/member")
  fun getMembers(@PathVariable groupId: Long): List<MemberListItemDto> {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.memberService.getMembers(
        userId = userId,
        groupId = groupId
    )
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  fun addNewGroup(
      @RequestBody @Valid requestBody: GroupAddRequestDto
  ): GroupAddResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.groupService.addNewGroup(
        userId = userId,
        name = requestBody.name!!
    )
  }

  @PostMapping("/{groupId}/member")
  @ResponseStatus(code = HttpStatus.CREATED)
  fun addMember(@PathVariable groupId: Long): MemberAddResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    val newMemberId = this.memberService.addMember(
        userId = userId,
        groupId = groupId
    )

    return MemberAddResponseDto(newMemberId = newMemberId)
  }

  @PutMapping("/{groupId}")
  fun updateGroup(
      @RequestBody @Valid requestBody: GroupUpdateRequestDto,
      @PathVariable groupId: Long
  ): GroupUpdateAndDeleteResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.groupService.updateGroup(
        userId = userId,
        groupId = groupId,
        name = requestBody.name,
        allowRegister = requestBody.allowRegister
    )
  }

  @PutMapping("/{groupId}/member/{memberId}")
  fun updateMember(
      @RequestBody @Valid requestBody: MemberUpdateRequestDto,
      @PathVariable groupId: Long,
      @PathVariable memberId: Long
  ): MemberUpdateAndDeleteResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    this.memberService.updateMember(
        userId = userId,
        memberId = memberId,
        enabled = requestBody.enabled,
        hasAdminPermission = requestBody.hasAdminPermission
    )

    return MemberUpdateAndDeleteResponseDto(memberId = memberId)
  }

  @DeleteMapping("/{groupId}")
  fun deleteGroup(@PathVariable groupId: Long): GroupUpdateAndDeleteResponseDto {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return this.groupService.deleteGroup(
        userId = userId,
        groupId = groupId
    )
  }

  @ExceptionHandler(value = [UserNotEnrolledToGroupException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun userNotEnrolledToGroupException(e: UserNotEnrolledToGroupException): MutableMap<String, Any?> {
    return ClientError4XX.USER_NOT_ENROLLED_TO_GROUP_ERROR
  }

  @ExceptionHandler(value = [GroupNotFoundException::class])
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  fun groupNotFoundException(e: GroupNotFoundException): MutableMap<String, Any?> {
    return ClientError4XX.GROUP_NOT_FOUND_ERROR
  }

  @ExceptionHandler(value = [CreatorModifyRequestedException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun creatorModifyRequestedException(e: CreatorModifyRequestedException): MutableMap<String, Any?> {
    return ClientError4XX.CREATOR_MODIFY_REQUESTED_ERROR
  }

  @ExceptionHandler(value = [PermissionDeniedException::class])
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  fun permissionDeniedException(e: PermissionDeniedException): MutableMap<String, Any?> {
    return ClientError4XX.PERMISSION_DENIED_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
}