package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderAddResponseDto(
    @JsonProperty("new_order_id")
    var newOrderId: Long
) {
  constructor() : this(0)
}