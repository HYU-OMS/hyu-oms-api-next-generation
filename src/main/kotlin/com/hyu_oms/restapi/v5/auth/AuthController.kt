package com.hyu_oms.restapi.v5.auth

import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import javax.validation.Valid

@RestController
@RequestMapping("/api/v5/auth")
class AuthController(
    private val authService: AuthService
) {
  // string to json 을 하기 위한 놈.
  private val objectMapper: ObjectMapper = ObjectMapper()

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

  // 외부 API 요청 (소셜계정 로그인) 오류 발생 시 여기로 넘어옴.
  @ExceptionHandler(value = [HttpClientErrorException::class])
  fun httpClientErrorExceptionHandler(e: HttpClientErrorException): ResponseEntity< MutableMap<String, Any?> > {
    val statusCode = e.statusCode
    val responseBody = ClientError4XX.REST_CLIENT_ERROR
    responseBody["data"] =
        if(e.responseBodyAsString.isNotEmpty()) {
          this.objectMapper.readValue(e.responseBodyAsString, MutableMap::class.java)
        }
        else {
          null
        }

    return ResponseEntity(responseBody, statusCode)
  }
}