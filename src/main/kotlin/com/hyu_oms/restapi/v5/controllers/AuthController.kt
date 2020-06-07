package com.hyu_oms.restapi.v5.controllers

import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hyu_oms.restapi.v5.dtos.auth.AuthTokenInitialIssueRequestDto
import com.hyu_oms.restapi.v5.dtos.auth.AuthTokenRefreshRequestDto
import com.hyu_oms.restapi.v5.dtos.auth.AuthTokenResponseDto
import com.hyu_oms.restapi.v5.exceptions.UnsupportedSocialMediaException
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.services.AuthService
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
  private val objectMapper: ObjectMapper = ObjectMapper() // string to json 을 하기 위한 놈.

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

  /**
   * 외부 API 요청 (소셜계정 로그인) 오류 발생 시 여기로 넘어옴.
   *
   * @return ClientError4XX.REST_CLIENT_ERROR with status code and message from external API server.
   */
  @ExceptionHandler(value = [HttpClientErrorException::class])
  fun httpClientErrorExceptionHandler(e: HttpClientErrorException): ResponseEntity<MutableMap<String, Any?>> {
    val statusCode = e.statusCode
    val responseBody = ClientError4XX.REST_CLIENT_ERROR
    responseBody["data"] =
        if (e.responseBodyAsString.isNotEmpty()) {
          this.objectMapper.readValue(e.responseBodyAsString, MutableMap::class.java)
        } else {
          null
        }

    return ResponseEntity(responseBody, statusCode)
  }

  /**
   * JWT initial issue, refresh issue 관련 exception 대응.
   *
   * @return ClientError4XX.JWT_CREATION_ERROR or ClientError4XX.JWT_VERIFICATION_ERROR with status code 403.
   */
  @ExceptionHandler(value = [
    JWTVerificationException::class,
    JWTCreationException::class
  ])
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  fun jwtExceptionHandler(e: Exception): MutableMap<String, Any?> {
    return when (e) {
      is JWTCreationException -> {
        val responseBody = ClientError4XX.JWT_CREATION_ERROR
        responseBody["data"] = mutableMapOf("message" to e.message)

        responseBody
      }
      is JWTVerificationException -> {
        val responseBody = ClientError4XX.JWT_VERIFICATION_ERROR
        responseBody["data"] = mutableMapOf("message" to e.message)

        responseBody
      }
      else -> throw e
    }
  }

  @ExceptionHandler(value = [UnsupportedSocialMediaException::class])
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  fun unsupportedSocialMediaException(e: UnsupportedSocialMediaException): MutableMap<String, Any?> {
    return ClientError4XX.UNSUPPORTED_SOCIAL_MEDIA_ERROR
  }
}