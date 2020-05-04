package com.hyu_oms.restapi.v5.menu

import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository: JpaRepository<Menu, Long> {
}