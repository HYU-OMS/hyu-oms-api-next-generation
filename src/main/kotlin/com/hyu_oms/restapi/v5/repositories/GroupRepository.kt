package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
  fun findDistinctByEnabledIsTrueAndMembersIn(
      members: List<Member>,
      pageable: Pageable
  ): Page<Group>

  fun findDistinctByEnabledIsTrueAndMembersIn(members: List<Member>): List<Group>

  fun findDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndIdIsNotIn(
      id: List<Long>,
      pageable: Pageable
  ): Page<Group>

  fun findByIdAndEnabledIsTrue(id: Long): Group?

  fun findByCreatorAndEnabledIsTrue(creator: User): Group?
}