package com.hyu_oms.restapi.v5.dtos.queue

import com.fasterxml.jackson.annotation.JsonProperty

data class QueueUpdateResponseDto(
    @JsonProperty("queue_id")
    var queueId: Long
) {
  constructor() : this(0)
}