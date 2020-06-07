package com.hyu_oms.restapi.v5.dtos.group

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class GroupAddRequestDto(
    @field:NotEmpty
    @JsonProperty("name")
    var name: String?
)