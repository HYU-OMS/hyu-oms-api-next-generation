package com.hyu_oms.restapi.v5.dtos.member

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class MemberUpdateRequestDto (
    @JsonProperty("enabled")
    var enabled: Boolean?,

    @JsonProperty("has_admin_permission")
    var hasAdminPermission: Boolean?
)