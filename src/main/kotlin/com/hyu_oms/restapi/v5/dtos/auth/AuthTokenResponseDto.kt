package com.hyu_oms.restapi.v5.dtos.auth

data class AuthTokenResponseDto(
    var access: String,
    var refresh: String
) {
  constructor() : this("", "")
}