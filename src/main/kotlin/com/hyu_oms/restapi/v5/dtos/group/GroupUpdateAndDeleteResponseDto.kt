package com.hyu_oms.restapi.v5.dtos.group

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupUpdateAndDeleteResponseDto(
    @JsonProperty("group_id")
    var groupId: Long
) {
  constructor() : this(0)
}