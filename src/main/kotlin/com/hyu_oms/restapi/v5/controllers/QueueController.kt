package com.hyu_oms.restapi.v5.controllers

import com.hyu_oms.restapi.v5.dtos.queue.QueueItemDto
import com.hyu_oms.restapi.v5.dtos.queue.QueueUpdateResponseDto
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.QueueItemAlreadyDeliveredException
import com.hyu_oms.restapi.v5.exceptions.QueueItemNotFoundException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.QueueService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v5/queue")
class QueueController(
    private val queueService: QueueService
) {
  @GetMapping
  @ResponseStatus(code = HttpStatus.OK)
  fun getQueueItems(
      @RequestParam(name = "group_id") groupId: Long
  ): List<QueueItemDto> {
    return this.queueService.getQueueItems(groupId = groupId)
  }

  @PatchMapping("/{queueId}")
  @ResponseStatus(code = HttpStatus.OK)
  fun updateQueue(@PathVariable queueId: Long): QueueUpdateResponseDto {
    return this.queueService.setQueueItemDeliveredAsTrue(queueId = queueId)
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

  @ExceptionHandler(value = [QueueItemNotFoundException::class])
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  fun queueItemNotFoundException(e: QueueItemNotFoundException): MutableMap<String, Any?> {
    return ClientError4XX.QUEUE_ITEM_NOT_FOUND_ERROR
  }

  @ExceptionHandler(value = [QueueItemAlreadyDeliveredException::class])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun queueItemAlreadyDeliveredException(e: QueueItemAlreadyDeliveredException): MutableMap<String, Any?> {
    return ClientError4XX.QUEUE_ITEM_ALREADY_DELIVERED_ERROR
  }
}