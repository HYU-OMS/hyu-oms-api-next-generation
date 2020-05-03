package com.hyu_oms.restapi.v5.auth

import com.hyu_oms.restapi.v5.social_account.SocialAccount
import com.hyu_oms.restapi.v5.social_account.SocialAccountRepository
import com.hyu_oms.restapi.v5.social_account.SocialAccountType
import com.hyu_oms.restapi.v5.user.User
import com.hyu_oms.restapi.v5.user.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.nio.charset.Charset

@Service
class AuthService(
    private val userService: UserService,
    private val socialAccountRepository: SocialAccountRepository,
    restTemplateBuilder: RestTemplateBuilder
) {
  private val restTemplate: RestTemplate = restTemplateBuilder.build()

  @Value("\${social.kakao.rest-api-key}")
  private lateinit var kakaoRestApiKey: String

  @Value("\${social.kakao.rest-api-secret}")
  private lateinit var kakaoRestApiSecret: String

  fun tokenInitialIssue(requestBody: AuthTokenInitialIssueRequestDto): AuthTokenResponseDto {
    val code = requestBody.code
    val redirectedUrl = requestBody.redirectedUrl

    // TODO: facebook 로그인 추가
    val targetUser: User =
    when(requestBody.socialAccountType) {
      "kakao" -> this.oauthWithKakao(code!!, redirectedUrl!!)
      else -> throw Exception() // TODO: configure appropriate exception
    }

    return AuthTokenResponseDto()
  }

  private fun oauthWithKakao(code: String, redirectedUrl: String): User {
    val urlForOauth = URI("https://kauth.kakao.com/oauth/token")

    val bodyForOauth: MultiValueMap<String, String> = LinkedMultiValueMap()
    bodyForOauth.add("grant_type", "authorization_code")
    bodyForOauth.add("client_id", this.kakaoRestApiKey)
    bodyForOauth.add("redirect_uri", redirectedUrl)
    bodyForOauth.add("code", code)
    bodyForOauth.add("client_secret", this.kakaoRestApiSecret)

    val headersForOauth = HttpHeaders()
    headersForOauth.contentType = MediaType.APPLICATION_FORM_URLENCODED
    headersForOauth.acceptCharset = listOf(Charset.forName("utf-8"))

    val requestForOauth = HttpEntity< MultiValueMap<String, String> >(bodyForOauth, headersForOauth)
    val tt = requestForOauth.body
    val t = tt.toString()

    val oauthResponseEntity: ResponseEntity< Map<*, *> >
    try {
      oauthResponseEntity = this.restTemplate.exchange(urlForOauth, HttpMethod.POST, requestForOauth, Map::class.java)
    } catch(e: HttpClientErrorException) {
      val stop = 0
      throw e
    }

    val oauthResponseBody = oauthResponseEntity.body!!

    val kakaoRestApiAccessToken: String = oauthResponseBody["access_token"].toString()

    val urlForUserInfo = URI("https://kapi.kakao.com/v2/user/me")
    val headersForUserInfo = HttpHeaders()
    headersForUserInfo.setBearerAuth(kakaoRestApiAccessToken)

    val httpEntityForUserInfo = HttpEntity(null, headersForUserInfo)

    val userInfoResponse: ResponseEntity< Map<*, *> >
    try {
      userInfoResponse = this.restTemplate.exchange(urlForUserInfo, HttpMethod.GET, httpEntityForUserInfo, Map::class.java)
    } catch(e: HttpClientErrorException) {
      val stop = 0
      throw e
    }

    val userInfoResponseBody = userInfoResponse.body!!

    val accountId: String = userInfoResponseBody["id"].toString()
    val accountName: String = (userInfoResponseBody["properties"] as Map<*, *>)["nickname"].toString()
//    if(userInfoResponse.statusCode == HttpStatus.OK) {
//
//    }
//    else {
//      throw Exception()
//    }

    var targetUser: User
    try {
      val targetSocialAccount = this.socialAccountRepository.findByAccountTypeAndAccountId(
          accountType = SocialAccountType.KAKAO,
          accountId = accountId
      )
      targetUser = targetSocialAccount.user
    } catch(e: EmptyResultDataAccessException) {
      val newUser: User = this.userService.addNewOne(
          name = accountName
      )

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
    val refreshToken = requestBody.refreshToken
    return AuthTokenResponseDto()
  }
}