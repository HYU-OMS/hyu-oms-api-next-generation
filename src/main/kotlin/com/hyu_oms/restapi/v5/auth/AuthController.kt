package com.hyu_oms.restapi.v5.auth

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/auth")
class AuthController(
    val authService: AuthService
) {
  @PostMapping("/initial")
  @ResponseStatus(code = HttpStatus.CREATED)
  fun tokenInitialIssueRequest(
      @RequestBody @Valid requestBody: AuthTokenInitialIssueRequestDto
  ): AuthTokenResponseDto {
    return this.authService.tokenInitialIssue(requestBody = requestBody)
  }

  @PostMapping("/refresh")
  fun tokenRefreshRequest(
      @RequestBody @Valid requestBody: AuthTokenRefreshRequestDto
  ): AuthTokenResponseDto {
    return this.authService.tokenRefresh(requestBody = requestBody)
  }
}