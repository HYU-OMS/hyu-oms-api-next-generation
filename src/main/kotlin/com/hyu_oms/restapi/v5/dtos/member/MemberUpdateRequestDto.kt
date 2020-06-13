package com.hyu_oms.restapi.v5.dtos.member

import com.fasterxml.jackson.annotation.JsonProperty

data class MemberUpdateRequestDto(
    @JsonProperty("enabled")
    var enabled: Boolean?,

    @JsonProperty("has_admin_permission")
    var hasAdminPermission: Boolean?
)