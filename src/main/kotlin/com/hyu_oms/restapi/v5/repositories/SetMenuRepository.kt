package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.SetMenu
import org.springframework.data.jpa.repository.JpaRepository

interface SetMenuRepository: JpaRepository<SetMenu, Long> {
}