package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty
import com.hyu_oms.restapi.v5.dtos.user.UserListItemDto
import com.hyu_oms.restapi.v5.enums.OrderStatus
import java.time.LocalDateTime
import java.time.ZoneId

data class OrderListItemDto(
    var id: Long,

    var user: UserListItemDto,
    var destination: String,

    @JsonProperty("total_price")
    var totalPrice: Int,
    var status: OrderStatus = OrderStatus.PD,

    @JsonProperty("created_at")
    var createdAt: LocalDateTime,

    @JsonProperty("updated_at")
    var updatedAt: LocalDateTime
) {
  constructor() : this(
      0,
      UserListItemDto(0, ""),
      "",
      0,
      OrderStatus.PD,
      LocalDateTime.now(ZoneId.of("UTC")),
      LocalDateTime.now(ZoneId.of("UTC"))
  )
}