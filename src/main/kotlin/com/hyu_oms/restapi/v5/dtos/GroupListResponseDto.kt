package com.hyu_oms.restapi.v5.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupListResponseDto(
    @JsonProperty("contents")
    var contents: List<GroupListItemDto>,

    @JsonProperty("total_pages")
    var totalPages: Int,

    @JsonProperty("total_elements")
    var totalElements: Long
) {
  constructor() : this(listOf(), 0, 0)
}