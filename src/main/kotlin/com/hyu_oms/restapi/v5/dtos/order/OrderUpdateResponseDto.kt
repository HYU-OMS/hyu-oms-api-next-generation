package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderUpdateResponseDto(
    @JsonProperty("order_id")
    var orderId: Long
) {
  constructor() : this(0)
}