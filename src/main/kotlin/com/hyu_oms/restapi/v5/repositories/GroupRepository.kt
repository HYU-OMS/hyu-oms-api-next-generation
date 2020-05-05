package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GroupRepository : JpaRepository<Group, Long> {
  fun findByEnabledIsTrueAndMembersInOrderByIdAsc(members: List<Member>, pageable: Pageable): Page<Group>
  fun countByEnabledIsTrueAndMembersIn(members: List<Member>): Long

  fun findByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotInOrderByIdAsc(members: List<Member>, pageable: Pageable): Page<Group>
  fun countByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotIn(members: List<Member>): Long

}