package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>