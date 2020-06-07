package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.group.GroupListResponseDto
import com.hyu_oms.restapi.v5.exceptions.UserNotEnrolledToGroupException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.GroupService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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

  ) {
    this.groupService.addNewGroup()
  }

  @ExceptionHandler(value = [UserNotEnrolledToGroupException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun userNotEnrolledToGroupException(e: UserNotEnrolledToGroupException): MutableMap<String, Any?> {
    return ClientError4XX.USER_NOT_ENROLLED_TO_GROUP_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
  // TODO: GroupNotFoundException Handler 작성
}