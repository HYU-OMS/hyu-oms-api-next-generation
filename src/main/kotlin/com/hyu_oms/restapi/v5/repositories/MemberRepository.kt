package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberRepository : JpaRepository<Member, Long> {
  @Query("SELECT m FROM Member m INNER JOIN FETCH m.group INNER JOIN FETCH m.user WHERE m.user = :user")
  fun findAllByUserUsingJoinFetch(@Param("user") user: User): List<Member>
}