package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.queue.QueueItemDto
import com.hyu_oms.restapi.v5.dtos.queue.QueueUpdateResponseDto
import com.hyu_oms.restapi.v5.entities.Queue
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.*
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.QueueRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class QueueService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val queueRepository: QueueRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }

  @Transactional(readOnly = true)
  fun getQueueItems(groupId: Long): List<QueueItemDto> {
    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    ) ?: throw PermissionDeniedException()

    val queueItems = this.queueRepository.findAllByGroupAndDeliveredIsFalse(group = group)
    return queueItems.stream()
        .map { queue: Queue ->
          this.modelMapper.map(
              queue,
              QueueItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())
  }

  @Transactional(readOnly = false)
  fun setQueueItemDeliveredAsTrue(queueId: Long): QueueUpdateResponseDto {
    val user = this.getUserFromContext()
    val queueItem = this.queueRepository.findByIdOrNull(
        id = queueId
    ) ?: throw QueueItemNotFoundException()

    if (queueItem.delivered) {
      throw QueueItemAlreadyDeliveredException()
    }

    val group = queueItem.group
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    )
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    queueItem.delivered = true
    this.queueRepository.save(queueItem)

    return QueueUpdateResponseDto(queueId = queueId)
  }
}