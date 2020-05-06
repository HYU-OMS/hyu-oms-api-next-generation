package com.hyu_oms.restapi.v5.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.ZoneId

data class GroupListItemDto(
    var id: Long?,
    var name: String,

    @JsonProperty("allow_register")
    var allowRegister: Boolean,

    @JsonProperty("created_at")
    var createdAt: LocalDateTime,

    @JsonProperty("updated_at")
    var updatedAt: LocalDateTime
) {
  constructor() : this(
      null,
      "",
      false,
      LocalDateTime.now(ZoneId.of("UTC")),
      LocalDateTime.now(ZoneId.of("UTC"))
  )
}