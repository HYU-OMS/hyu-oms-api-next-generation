package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.menu.MenuContentItemDto
import com.hyu_oms.restapi.v5.dtos.order.OrderAddResponseDto
import com.hyu_oms.restapi.v5.dtos.order.OrderListItemDto
import com.hyu_oms.restapi.v5.dtos.order.OrderListResponseDto
import com.hyu_oms.restapi.v5.dtos.order.OrderUpdateResponseDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuContentItemDto
import com.hyu_oms.restapi.v5.entities.Order
import com.hyu_oms.restapi.v5.entities.OrderItem
import com.hyu_oms.restapi.v5.entities.Queue
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.enums.OrderStatus
import com.hyu_oms.restapi.v5.exceptions.*
import com.hyu_oms.restapi.v5.repositories.*
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class OrderService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val menuRepository: MenuRepository,
    private val setMenuRepository: SetMenuRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val queueRepository: QueueRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }

  private fun generateOrderListResponseDto(pages: Page<Order>): OrderListResponseDto {
    return OrderListResponseDto(
        contents = pages.stream()
            .map { order: Order ->
              this.modelMapper.map(
                  order,
                  OrderListItemDto::class.javaObjectType
              )
            }
            .collect(Collectors.toList()),
        totalPages = pages.totalPages,
        totalElements = pages.totalElements
    )
  }

  @Transactional(readOnly = true)
  fun getAllOrderList(
      groupId: Long,
      page: Int = 0,
      size: Int = 20
  ): OrderListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").descending())

    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    ) ?: throw PermissionDeniedException()

    val pages = this.orderRepository.findAllByGroup(
        group = group,
        pageable = pageRequest
    )

    return this.generateOrderListResponseDto(pages = pages)
  }

  @Transactional(readOnly = true)
  fun getAllPendingOrderList(
      groupId: Long,
      page: Int = 0,
      size: Int = 20
  ): OrderListResponseDto {
    val pageRequest = PageRequest.of(page, size, Sort.by("id").ascending())

    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    ) ?: throw PermissionDeniedException()

    val pages = this.orderRepository.findAllByGroupAndStatus(
        group = group,
        pageable = pageRequest
    )

    return this.generateOrderListResponseDto(pages = pages)
  }

  @Transactional(readOnly = false)
  fun addNewOrder(
      groupId: Long,
      destination: String,
      menuContents: List<MenuContentItemDto>,
      setMenuContents: List<SetMenuContentItemDto>
  ): OrderAddResponseDto {
    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()

    this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    ) ?: throw PermissionDeniedException()

    val menuAmountMap = hashMapOf<Long, Int>()
    for (menuContent in menuContents) {
      menuAmountMap[menuContent.menuId!!] = menuContent.amount!!
    }

    val setMenuAmountMap = hashMapOf<Long, Int>()
    for (setMenuContent in setMenuContents) {
      setMenuAmountMap[setMenuContent.setMenuId!!] = setMenuContent.amount!!
    }

    var totalPrice = 0

    val menus = this.menuRepository.findDistinctByIdIn(ids = menuContents.map { it.menuId!! })
    for (menu in menus) {
      if (!menu.enabled) {
        throw DisabledMenuOrSetMenuRequestedException()
      }

      totalPrice += menu.price * menuAmountMap[menu.id]!!
    }

    val setMenus = this.setMenuRepository.findDistinctByIdIn(ids = setMenuContents.map { it.setMenuId!! })
    for (setMenu in setMenus) {
      if (!setMenu.enabled) {
        throw DisabledMenuOrSetMenuRequestedException()
      }

      totalPrice += setMenu.price * setMenuAmountMap[setMenu.id]!!
    }

    val newOrder = Order(
        user = user,
        group = group,
        destination = destination,
        totalPrice = totalPrice
    )
    this.orderRepository.save(newOrder)

    val newOrderItems = mutableListOf<OrderItem>()

    for (menu in menus) {
      newOrderItems.add(OrderItem(
          order = newOrder,
          menuItem = menu,
          menuAmount = menuAmountMap[menu.id]
      ))
    }

    for (setMenu in setMenus) {
      newOrderItems.add(OrderItem(
          order = newOrder,
          setMenuItem = setMenu,
          setMenuAmount = setMenuAmountMap[setMenu.id]
      ))
    }

    this.orderItemRepository.saveAll(newOrderItems)

    return OrderAddResponseDto(newOrderId = newOrder.id)
  }

  @Transactional(readOnly = false)
  fun updateOrder(orderId: Long, isApproved: Boolean): OrderUpdateResponseDto {
    val user = this.getUserFromContext()
    val order = this.orderRepository.findByIdOrNull(id = orderId) ?: throw OrderNotFoundException()
    if (order.status != OrderStatus.PD) {
      throw OrderIsNotPendingStatusException()
    }

    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = order.group
    )
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    if (isApproved) {
      order.status = OrderStatus.AP

      val queues = mutableListOf<Queue>()
      for (orderItem in order.orderItems) {
        if (orderItem.menuItem != null) {
          // Menu
          queues.add(Queue(
              order = order,
              menu = orderItem.menuItem!!,
              destination = order.destination,
              amount = orderItem.menuAmount!!
          ))
        } else {
          // SetMenu
          for (setMenuContent in orderItem.setMenuItem!!.setMenuContents) {
            val menu = setMenuContent.menu
            val amount = orderItem.setMenuAmount!! * setMenuContent.amount

            queues.add(Queue(
                order = order,
                menu = menu,
                destination = order.destination,
                amount = amount
            ))
          }
        }
      }

      this.queueRepository.saveAll(queues)
    } else {
      order.status = OrderStatus.RJ
    }

    this.orderRepository.save(order)

    return OrderUpdateResponseDto(orderId = orderId)
  }
}