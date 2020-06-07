package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.SocialAccount
import com.hyu_oms.restapi.v5.enums.SocialAccountType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialAccountRepository : JpaRepository<SocialAccount, Long> {
  fun findByAccountTypeAndAccountId(
      accountType: SocialAccountType,
      accountId: String
  ): SocialAccount
}