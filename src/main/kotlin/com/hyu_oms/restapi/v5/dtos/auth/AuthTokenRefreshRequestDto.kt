package com.hyu_oms.restapi.v5.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class AuthTokenRefreshRequestDto(
    @field:NotEmpty
    @JsonProperty("refresh")
    var refresh: String
)