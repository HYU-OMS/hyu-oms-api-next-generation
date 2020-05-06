package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupRepository : JpaRepository<Group, Long> {
  @Query(
      value = """
        SELECT DISTINCT g 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        INNER JOIN FETCH g.creator 
        WHERE 
          g.enabled = true AND m IN :members 
        ORDER BY g.id ASC
      """,
      countQuery = """
        SELECT DISTINCT COUNT(g) 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        WHERE 
          g.enabled = true AND m IN :members
      """
  )
  fun findDistinctByEnabledIsTrueAndMembersInOrderByIdAsc(
      @Param("members") members: List<Member>,
      pageable: Pageable
  ): Page<Group>

  fun countDistinctByEnabledIsTrueAndMembersIn(members: List<Member>): Long

  @Query(
      value = """
        SELECT DISTINCT g 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        INNER JOIN FETCH g.creator 
        WHERE
          g.enabled = true AND 
          g.allowRegister = true AND 
          m NOT IN :members 
        ORDER BY g.id ASC
      """,
      countQuery = """
        SELECT DISTINCT COUNT(g) 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        WHERE 
          g.enabled = true AND 
          g.allowRegister = true AND 
          m NOT IN :members
      """
  )
  fun findDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotInOrderByIdAsc(
      @Param("members") members: List<Member>,
      pageable: Pageable
  ): Page<Group>

  fun countDistinctByEnabledIsTrueAndAllowRegisterIsTrueAndMembersNotIn(members: List<Member>): Long
}