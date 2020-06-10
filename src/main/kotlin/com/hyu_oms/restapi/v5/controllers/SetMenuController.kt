package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuAddRequestDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuListItemDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuUpdateRequestDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuUpdateResponseDto
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.SetMenuNotFoundException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.SetMenuService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/setmenu")
class SetMenuController(
    private val setMenuService: SetMenuService
) {
  @GetMapping
  fun getSetMenuList(
      @RequestParam(name = "group_id") groupId: Long
  ): List<SetMenuListItemDto> {
    return this.setMenuService.getSetMenuList(groupId = groupId)
  }

  @PostMapping
  fun addSetMenu(
      @RequestBody @Valid requestBody: SetMenuAddRequestDto
  ) {
    this.setMenuService.addSetMenu(
        groupId = requestBody.groupId!!,
        name = requestBody.name!!,
        price = requestBody.price!!,
        menuContents = requestBody.menuContents!!
    )
  }

  @PutMapping("/{setMenuId}")
  fun updateMenu(
      @PathVariable setMenuId: Long,
      @RequestBody @Valid requestBody: SetMenuUpdateRequestDto
  ): SetMenuUpdateResponseDto {
    return this.setMenuService.updateSetMenu(
        setMenuId = setMenuId,
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

  @ExceptionHandler(value = [SetMenuNotFoundException::class])
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  fun setMenuNotFoundException(e: SetMenuNotFoundException): MutableMap<String, Any?> {
    return ClientError4XX.SET_MENU_NOT_FOUND_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
}