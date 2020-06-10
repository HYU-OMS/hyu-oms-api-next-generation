package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderListResponseDto(
    @JsonProperty("contents")
    var contents: List<OrderListItemDto>,

    @JsonProperty("total_pages")
    var totalPages: Int,

    @JsonProperty("total_elements")
    var totalElements: Long
) {
  constructor() : this(listOf(), 0, 0)
}