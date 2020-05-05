package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, Long> {
  @Query("SELECT m FROM Member m INNER JOIN FETCH m.group WHERE m.user = ?1")
  fun findByUser(user: User): List<Member>
}