package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {


  fun findAllByUserAndEnabledIsTrue(user: User): List<Member>

  fun findAllByUserIdAndEnabledIsTrue(userId: Long): List<Member>
}