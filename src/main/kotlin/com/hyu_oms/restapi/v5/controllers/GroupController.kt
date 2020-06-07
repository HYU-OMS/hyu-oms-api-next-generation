package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.group.*
import com.hyu_oms.restapi.v5.exceptions.GroupAlreadyCreatedIn12HoursException
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.UserNotEnrolledToGroupException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.GroupService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/group")
class GroupController(
    private val groupService: GroupService
) {
  @GetMapping("/enrolled")
  fun getEnrolledGroupList(
      @RequestParam(defaultValue = "0") page: Int,
      @RequestParam(defaultValue = "20") size: Int
  ): GroupListResponseDto {
    return this.groupService.getEnrolledList(page = page, size = size)
  }

  @GetMapping("/not-enrolled")
  fun getNotEnrolledGroupList(
      @RequestParam(defaultValue = "0") page: Int,
      @RequestParam(defaultValue = "20") size: Int
  ): GroupListResponseDto {
    return this.groupService.getNotEnrolledAndRegisterAllowedList(page = page, size = size)
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  fun addNewGroup(
    @RequestBody @Valid requestBody: GroupAddRequestDto
  ): GroupAddResponseDto {
    return this.groupService.addNewGroup(name = requestBody.name!!)
  }

  @PutMapping("/{groupId}")
  fun updateGroup(
      @RequestBody @Valid requestBody: GroupUpdateRequestDto,
      @PathVariable groupId: Long
  ): GroupUpdateResponseDto {
    return this.groupService.updateGroup(
        groupId = groupId,
        name = requestBody.name,
        allowRegister = requestBody.allowRegister
    )
  }

  @ExceptionHandler(value = [UserNotEnrolledToGroupException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun userNotEnrolledToGroupException(e: UserNotEnrolledToGroupException): MutableMap<String, Any?> {
    return ClientError4XX.USER_NOT_ENROLLED_TO_GROUP_ERROR
  }

  @ExceptionHandler(value = [GroupAlreadyCreatedIn12HoursException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun groupAlreadyCreatedIn12HoursException(e: GroupAlreadyCreatedIn12HoursException): MutableMap<String, Any?> {
    return ClientError4XX.GROUP_ALREADY_CREATED_IN_12_HOURS_ERROR
  }

  @ExceptionHandler(value = [GroupNotFoundException::class])
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  fun groupNotFoundException(e: GroupNotFoundException): MutableMap<String, Any?> {
    return ClientError4XX.GROUP_NOT_FOUND_ERROR
  }

  @ExceptionHandler(value = [PermissionDeniedException::class])
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  fun permissionDeniedException(e: PermissionDeniedException): MutableMap<String, Any?> {
    return ClientError4XX.PERMISSION_DENIED_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
}