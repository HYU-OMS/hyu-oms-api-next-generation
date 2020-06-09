package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
  fun findAllByUserAndEnabledIsTrue(user: User): List<Member>

  fun findByUserAndGroupAndEnabledIsTrue(user: User, group: Group): Member?

  @Query(
      value = """
        DELETE FROM Member m
        WHERE m.group = :group
      """
  )
  @Modifying
  fun deleteMembersByGroup(@Param("group") group: Group)
}