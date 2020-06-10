package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.SetMenu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetMenuRepository : JpaRepository<SetMenu, Long> {
  fun findAllByGroup(group: Group): List<SetMenu>

  fun findDistinctByIdIn(ids: List<Long>): List<SetMenu>
}