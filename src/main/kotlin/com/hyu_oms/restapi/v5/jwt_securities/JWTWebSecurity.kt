package com.hyu_oms.restapi.v5.jwt_securities

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@EnableWebSecurity
class JWTWebSecurity: WebSecurityConfigurerAdapter() {
  @Value("\${jwt.secret-key}")
  private lateinit var jwtSecretKey: String

  @Value("\${jwt.issuer}")
  private lateinit var jwtIssuer: String

  override fun configure(http: HttpSecurity?) {
    http!!
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(JWTAuthorizationFilter(
            this.authenticationManager(),
            this.jwtSecretKey,
            this.jwtIssuer
        ))
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/v5/auth/initial").permitAll()
        .antMatchers(HttpMethod.POST, "/api/v5/auth/refresh").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v5/health-check").permitAll()
        .anyRequest().authenticated()
        .and()
        .exceptionHandling()
        .accessDeniedHandler(JWTAccessDeniedHandler())
        .authenticationEntryPoint(JWTUnauthorizedHandler())
  }
}