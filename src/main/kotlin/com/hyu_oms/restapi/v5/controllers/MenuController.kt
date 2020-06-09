package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.menu.*
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.MenuNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.MenuService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/menu")
class MenuController(
    private val menuService: MenuService
) {
  @GetMapping
  fun getMenuList(
      @RequestParam(name = "group_id") groupId: Long
  ): List<MenuListItemDto> {
    return this.menuService.getMenuList(groupId = groupId)
  }

  @PostMapping
  fun addMenu(
      @RequestBody @Valid requestBody: MenuAddRequestDto
  ): MenuAddResponseDto {
    return this.menuService.addMenu(
        groupId = requestBody.groupId!!,
        name = requestBody.name!!,
        price = requestBody.price!!
    )
  }

  @PutMapping("/{menuId}")
  fun updateMenu(
      @PathVariable menuId: Long,
      @RequestBody @Valid requestBody: MenuUpdateRequestDto
  ): MenuUpdateResponseDto {
    return this.menuService.updateMenu(
        menuId = menuId,
        price = requestBody.price,
        enabled = requestBody.enabled
    )
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

  @ExceptionHandler(value = [MenuNotFoundException::class])
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  fun menuNotFoundException(e: MenuNotFoundException): MutableMap<String, Any?> {
    return ClientError4XX.MENU_NOT_FOUND_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
}