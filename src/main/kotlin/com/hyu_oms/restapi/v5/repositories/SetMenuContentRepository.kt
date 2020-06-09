package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.SetMenu
import com.hyu_oms.restapi.v5.entities.SetMenuContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SetMenuContentRepository : JpaRepository<SetMenuContent, Long> {
  fun findDistinctBySetMenuIn(setMenus: List<SetMenu>): List<SetMenuContent>
}