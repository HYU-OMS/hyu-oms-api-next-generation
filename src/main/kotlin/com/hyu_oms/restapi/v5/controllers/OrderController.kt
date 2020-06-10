package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.order.*
import com.hyu_oms.restapi.v5.exceptions.DisabledMenuOrSetMenuRequestedException
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.OrderIsNotPendingStatusException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/order")
class OrderController(
    private val orderService: OrderService
) {
  @GetMapping
  fun getOrderList(
      @RequestParam(name = "group_id") groupId: Long,
      @RequestParam(defaultValue = "0") page: Int,
      @RequestParam(defaultValue = "20") size: Int,
      @RequestParam(name = "pending_only", defaultValue = "0") pendingOnly: Int
  ): OrderListResponseDto {
    return if (pendingOnly == 0) {
      this.orderService.getAllOrderList(
          groupId = groupId,
          page = page,
          size = size
      )
    } else {
      this.orderService.getAllPendingOrderList(
          groupId = groupId,
          page = page,
          size = size
      )
    }
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  fun addNewOrder(@RequestBody @Valid requestBody: OrderAddRequestDto): OrderAddResponseDto {
    return this.orderService.addNewOrder(
        groupId = requestBody.groupId!!,
        destination = requestBody.destination!!,
        menuContents = requestBody.menuContents!!,
        setMenuContents = requestBody.setMenuContents!!
    )
  }

  @PatchMapping("/{orderId}")
  fun updateOrder(
      @PathVariable orderId: Long,
      @RequestBody @Valid requestBody: OrderUpdateRequestDto
  ): OrderUpdateResponseDto {
    return this.orderService.updateOrder(
        orderId = orderId,
        isApproved = requestBody.isApproved!!
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

  @ExceptionHandler(value = [DisabledMenuOrSetMenuRequestedException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun disabledMenuOrSetMenuRequestException(e: DisabledMenuOrSetMenuRequestedException): MutableMap<String, Any?> {
    return ClientError4XX.DISABLED_MENU_OR_SET_MENU_REQUESTED_ERROR
  }

  @ExceptionHandler(value = [OrderIsNotPendingStatusException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun orderIsNotPendingStatusException(e: OrderIsNotPendingStatusException): MutableMap<String, Any?> {
    return ClientError4XX.ORDER_IS_NOT_PENDING_STATUS_ERROR
  }

  // TODO: UserNotFoundException Handler 작성
}