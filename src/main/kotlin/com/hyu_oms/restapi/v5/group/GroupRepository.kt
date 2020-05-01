package com.hyu_oms.restapi.v5.group

import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository: JpaRepository<Group, Long> {
}