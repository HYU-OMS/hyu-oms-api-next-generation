package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class OrderUpdateRequestDto(
    @field:NotEmpty
    @JsonProperty("is_approved")
    var isApproved: Boolean?
)