package com.hyu_oms.restapi.v5.auth

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class AuthTokenRefreshRequestDto(
    @field:NotEmpty
    @JsonProperty("refresh_token")
    var refreshToken: String
)