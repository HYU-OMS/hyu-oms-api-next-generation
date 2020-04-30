package com.hyu_oms.restapi.v5.setmenu

import org.springframework.data.jpa.repository.JpaRepository

interface SetMenuRepository: JpaRepository<SetMenu, Long> {
}