package com.hyu_oms.restapi.v5.dtos

data class GroupListResponseDto(
    var list: List<GroupListItemDto>,
    var count: Long
) {
  constructor() : this(listOf(), 0)
}