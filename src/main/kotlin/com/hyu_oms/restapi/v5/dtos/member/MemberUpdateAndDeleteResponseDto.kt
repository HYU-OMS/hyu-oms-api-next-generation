package com.hyu_oms.restapi.v5.dtos.member

import com.fasterxml.jackson.annotation.JsonProperty

data class MemberUpdateAndDeleteResponseDto(
    @JsonProperty("member_id")
    var memberId: Long
) {
  constructor() : this(0)
}