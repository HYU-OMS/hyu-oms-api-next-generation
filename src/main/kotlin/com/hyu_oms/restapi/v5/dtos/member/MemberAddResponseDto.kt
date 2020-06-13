package com.hyu_oms.restapi.v5.dtos.member

import com.fasterxml.jackson.annotation.JsonProperty

data class MemberAddResponseDto(
    @JsonProperty("new_member_id")
    var newMemberId: Long
) {
  constructor(): this(0)
}