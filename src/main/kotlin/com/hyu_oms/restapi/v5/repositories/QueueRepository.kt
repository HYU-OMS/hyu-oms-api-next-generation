package com.hyu_oms.restapi.v5.repositories

import com.hyu_oms.restapi.v5.entities.Queue
import org.springframework.data.jpa.repository.JpaRepository

interface QueueRepository: JpaRepository<Queue, Long> {
}