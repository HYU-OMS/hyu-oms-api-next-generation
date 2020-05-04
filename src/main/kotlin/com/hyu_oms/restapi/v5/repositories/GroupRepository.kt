package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long>