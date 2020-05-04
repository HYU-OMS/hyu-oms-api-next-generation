package com.hyu_oms.restapi.v5.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.hyu_oms.restapi.v5.dtos.AuthTokenInitialIssueRequestDto
import com.hyu_oms.restapi.v5.dtos.AuthTokenRefreshRequestDto
import com.hyu_oms.restapi.v5.dtos.AuthTokenResponseDto
import com.hyu_oms.restapi.v5.entities.SocialAccount
import com.hyu_oms.restapi.v5.repositories.SocialAccountRepository
import com.hyu_oms.restapi.v5.enums.SocialAccountType
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.UnsupportedSocialMediaException
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.nio.charset.Charset
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val socialAccountRepository: SocialAccountRepository,
    restTemplateBuilder: RestTemplateBuilder
) {
  private val restTemplate: RestTemplate = restTemplateBuilder.build()

  @Value("\${social.kakao.rest-api-key}")
  private lateinit var kakaoRestApiKey: String

  @Value("\${social.kakao.rest-api-secret}")
  private lateinit var kakaoRestApiSecret: String

  @Value("\${jwt.secret-key}")
  private lateinit var jwtSecretKey: String

  @Value("\${jwt.issuer}")
  private lateinit var jwtIssuer: String

  @Value("\${jwt.access-token.lifetime}")
  private lateinit var jwtAccessTokenLifetime: String

  @Value("\${jwt.refresh-token.lifetime}")
  private lateinit var jwtRefreshTokenLifetime: String

  fun tokenInitialIssue(requestBody: AuthTokenInitialIssueRequestDto): AuthTokenResponseDto {
    val code = requestBody.code
    val redirectedUrl = requestBody.redirectedUrl

    // TODO: facebook 로그인 추가
    val targetUser: User =
    when(requestBody.socialAccountType) {
      "kakao" -> this.oauthWithKakao(code!!, redirectedUrl!!)
      else -> throw UnsupportedSocialMediaException()
    }

    val jwtAlgorithm: Algorithm = Algorithm.HMAC512(this.jwtSecretKey)

    val currentDate = Date()
    val calendar = Calendar.getInstance()

    calendar.time = currentDate
    calendar.add(Calendar.SECOND, jwtAccessTokenLifetime.toInt())
    val accessTokenExpiredDate = calendar.time

    calendar.time = currentDate
    calendar.add(Calendar.SECOND, jwtRefreshTokenLifetime.toInt())
    val refreshTokenExpiredDate = calendar.time

    val accessToken = JWT.create()
        .withIssuedAt(currentDate)
        .withNotBefore(currentDate)
        .withExpiresAt(accessTokenExpiredDate)
        .withJWTId(UUID.randomUUID().toString())
        .withIssuer(this.jwtIssuer)
        .withClaim("token_type", "access")
        .withClaim("user_id", targetUser.id)
        .sign(jwtAlgorithm)

    val refreshToken = JWT.create()
        .withIssuedAt(currentDate)
        .withNotBefore(currentDate)
        .withExpiresAt(refreshTokenExpiredDate)
        .withJWTId(UUID.randomUUID().toString())
        .withIssuer(this.jwtIssuer)
        .withClaim("token_type", "refresh")
        .withClaim("user_id", targetUser.id)
        .sign(jwtAlgorithm)

    return AuthTokenResponseDto(accessToken, refreshToken)
  }

  @Transactional
  fun oauthWithKakao(code: String, redirectedUrl: String): User {
    val urlForOauth = URI("https://kauth.kakao.com/oauth/token")

    val bodyForOauth: MultiValueMap<String, String> = LinkedMultiValueMap()
    bodyForOauth.add("grant_type", "authorization_code")
    bodyForOauth.add("client_id", this.kakaoRestApiKey)
    bodyForOauth.add("redirect_uri", redirectedUrl)
    bodyForOauth.add("code", code)
    bodyForOauth.add("client_secret", this.kakaoRestApiSecret) // 이거 빼먹으면 401 응답인데 body 가 텅 비어있음.

    val headersForOauth = HttpHeaders()
    headersForOauth.contentType = MediaType.APPLICATION_FORM_URLENCODED
    headersForOauth.acceptCharset = listOf(Charset.forName("utf-8"))

    val requestForOauth = HttpEntity< MultiValueMap<String, String> >(bodyForOauth, headersForOauth)

    val oauthResponse = this.restTemplate.exchange(urlForOauth, HttpMethod.POST, requestForOauth, Map::class.java)
    val oauthResponseBody = oauthResponse.body!!

    val kakaoRestApiAccessToken: String = oauthResponseBody["access_token"].toString()

    val urlForUserInfo = URI("https://kapi.kakao.com/v2/user/me")
    val headersForUserInfo = HttpHeaders()
    headersForUserInfo.setBearerAuth(kakaoRestApiAccessToken)

    val httpEntityForUserInfo = HttpEntity(null, headersForUserInfo)

    val userInfoResponse = this.restTemplate.exchange(urlForUserInfo, HttpMethod.GET, httpEntityForUserInfo, Map::class.java)
    val userInfoResponseBody = userInfoResponse.body!!

    val accountId: String = userInfoResponseBody["id"].toString()
    val accountName: String = (userInfoResponseBody["properties"] as Map<*, *>)["nickname"].toString()

    var targetUser: User
    try {
      val targetSocialAccount = this.socialAccountRepository.findByAccountTypeAndAccountId(
          accountType = SocialAccountType.KAKAO,
          accountId = accountId
      )

      targetUser = targetSocialAccount.user
    } catch(e: EmptyResultDataAccessException) {
      val newUser = User(name = accountName)
      this.userRepository.save(newUser)

      val newSocialAccount = SocialAccount(
          user = newUser,
          accountType = SocialAccountType.KAKAO,
          accountId = accountId
      )
      this.socialAccountRepository.save(newSocialAccount)

      targetUser = newUser
    }

    return targetUser
  }

  fun tokenRefresh(requestBody: AuthTokenRefreshRequestDto): AuthTokenResponseDto {
    val refreshToken = requestBody.refresh

    val jwtAlgorithm: Algorithm = Algorithm.HMAC512(this.jwtSecretKey)
    val jwtVerifier: JWTVerifier = JWT.require(jwtAlgorithm).withIssuer(this.jwtIssuer).build()

    val decodedJwt = jwtVerifier.verify(refreshToken)
    val userId = decodedJwt.getClaim("user_id").asInt()

    val currentDate = Date()
    val calendar = Calendar.getInstance()

    calendar.time = currentDate
    calendar.add(Calendar.SECOND, jwtAccessTokenLifetime.toInt())
    val accessTokenExpiredDate = calendar.time

    val accessToken = JWT.create()
        .withIssuedAt(currentDate)
        .withNotBefore(currentDate)
        .withExpiresAt(accessTokenExpiredDate)
        .withJWTId(UUID.randomUUID().toString())
        .withIssuer(this.jwtIssuer)
        .withClaim("token_type", "access")
        .withClaim("user_id", userId)
        .sign(jwtAlgorithm)

    return AuthTokenResponseDto(accessToken, refreshToken)
  }
}