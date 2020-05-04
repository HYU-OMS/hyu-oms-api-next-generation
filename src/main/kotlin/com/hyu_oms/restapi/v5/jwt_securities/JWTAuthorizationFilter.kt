package com.hyu_oms.restapi.v5.jwt_securities

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.ObjectMapper
import com.hyu_oms.restapi.v5.responses.ClientError4XX
import com.hyu_oms.restapi.v5.responses.ServerError5XX
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    authManager: AuthenticationManager,
    private val jwtSecretKey: String,
    private val jwtIssuer: String
): BasicAuthenticationFilter(authManager) {
  private val objectMapper: ObjectMapper = ObjectMapper() // string to json 을 하기 위한 놈.

  override fun doFilterInternal(
      request: HttpServletRequest,
      response: HttpServletResponse,
      chain: FilterChain
  ) {
    try {
      val header: String? = request.getHeader("Authorization")
      if(header != null && header.startsWith("Bearer")) {
        val accessToken = header.replace("Bearer", "").replace(" ", "")

        val jwtAlgorithm: Algorithm = Algorithm.HMAC512(this.jwtSecretKey)
        val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).withIssuer(this.jwtIssuer).build()

        val decodedJwt = jwtVerifier.verify(accessToken)
        val tokenType = decodedJwt.getClaim("token_type").asString()
        if(tokenType != "access") {
          throw JWTVerificationException("Provided token is not 'access_token'.")
        }

        val userId = decodedJwt.getClaim("user_id").asLong()
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            userId,
            null,
            arrayListOf()
        )
      }
    } catch(e: Exception) {
      val responseBody: MutableMap<String, Any?>
      val statusCode: HttpStatus

      when(e) {
        is JWTVerificationException -> {
          responseBody = ClientError4XX.JWT_VERIFICATION_ERROR
          responseBody["data"] = mutableMapOf("message" to e.message)
          statusCode = HttpStatus.FORBIDDEN
        }
        is TokenExpiredException -> {
          responseBody = ClientError4XX.JWT_EXPIRED_ERROR
          responseBody["data"] = mutableMapOf("message" to e.message)
          statusCode = HttpStatus.FORBIDDEN
        }
        else -> {
          responseBody = ServerError5XX.INTERNAL_SERVER_ERROR
          responseBody["data"] = mutableMapOf("message" to e.message)
          statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        }
      }

      response.status = statusCode.value()
      response.addHeader("Content-Type", "application/json")
      response.characterEncoding = "UTF-8"
      response.writer.write(this.objectMapper.writeValueAsString(responseBody))
      response.writer.flush()

      return
    }

    chain.doFilter(request, response)
  }
}