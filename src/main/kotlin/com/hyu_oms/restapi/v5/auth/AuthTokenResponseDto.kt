package com.hyu_oms.restapi.v5.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthTokenResponseDto(
    @JsonProperty("access_token")
    var accessToken: String,

    @JsonProperty("refresh_token")
    var refreshToken: String
) {
  constructor(): this("", "")
}