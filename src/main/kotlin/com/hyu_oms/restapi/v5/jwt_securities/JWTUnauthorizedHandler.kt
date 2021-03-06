package com.hyu_oms.restapi.v5.jwt_securities

import com.fasterxml.jackson.databind.ObjectMapper
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTUnauthorizedHandler : AuthenticationEntryPoint {
  private val objectMapper: ObjectMapper = ObjectMapper() // string to json 을 하기 위한 놈.

  override fun commence(
      request: HttpServletRequest,
      response: HttpServletResponse,
      authException: AuthenticationException
  ) {
    response.status = HttpStatus.UNAUTHORIZED.value()
    response.addHeader("Content-Type", "application/json")
    response.characterEncoding = "UTF-8"
    response.writer.write(this.objectMapper.writeValueAsString(ClientError4XX.JWT_UNAUTHORIZED))
    response.writer.flush()
  }
}