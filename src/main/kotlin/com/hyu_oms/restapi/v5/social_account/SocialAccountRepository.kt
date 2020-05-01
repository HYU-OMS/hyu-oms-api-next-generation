package com.hyu_oms.restapi.v5.social_account

import org.springframework.data.jpa.repository.JpaRepository

interface SocialAccountRepository: JpaRepository<SocialAccount, Long> {
}