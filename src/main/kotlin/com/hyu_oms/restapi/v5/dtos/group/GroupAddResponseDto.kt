package com.hyu_oms.restapi.v5.dtos.group

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupAddResponseDto(
    @JsonProperty("new_group_id")
    var newGroupId: Long
) {
  constructor() : this(0)
}