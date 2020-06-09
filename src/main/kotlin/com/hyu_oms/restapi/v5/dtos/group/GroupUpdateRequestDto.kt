package com.hyu_oms.restapi.v5.dtos.group

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupUpdateRequestDto(
    // TODO: Blank 에 대해서 처리 필요
    @JsonProperty("name")
    var name: String?,

    @JsonProperty("allow_register")
    var allowRegister: Boolean?
)