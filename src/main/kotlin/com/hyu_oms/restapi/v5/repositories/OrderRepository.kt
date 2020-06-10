package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Order
import com.hyu_oms.restapi.v5.enums.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
  fun findAllByGroup(group: Group, pageable: Pageable): Page<Order>

  fun findAllByGroupAndStatus(group: Group, status: OrderStatus = OrderStatus.PD, pageable: Pageable): Page<Order>
}