package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository: JpaRepository<Menu, Long> {
}