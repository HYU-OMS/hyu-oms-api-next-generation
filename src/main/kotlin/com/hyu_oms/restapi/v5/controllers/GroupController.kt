package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.GroupListResponseDto
import com.hyu_oms.restapi.v5.services.GroupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

  // TODO: UserNotFoundException Handler 작성
}