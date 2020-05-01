package com.hyu_oms.restapi.v5.queue

import org.springframework.data.jpa.repository.JpaRepository

interface QueueRepository: JpaRepository<Queue, Long> {
}