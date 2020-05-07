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
      """,
      countQuery = """
        SELECT COUNT(DISTINCT g) 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        WHERE 
          g.enabled = true AND m IN :members
      """
  )
  fun findAllEnrolled(
      @Param("members") members: List<Member>,
      pageable: Pageable
  ): Page<Group>

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
      """,
      countQuery = """
        SELECT COUNT(DISTINCT g) 
        FROM Group g 
        INNER JOIN Member m ON g = m.group 
        WHERE 
          g.enabled = true AND 
          g.allowRegister = true AND 
          m NOT IN :members
      """
  )
  fun findAllNotEnrolledAndRegisterAllowed(
      @Param("members") members: List<Member>,
      pageable: Pageable
  ): Page<Group>
}