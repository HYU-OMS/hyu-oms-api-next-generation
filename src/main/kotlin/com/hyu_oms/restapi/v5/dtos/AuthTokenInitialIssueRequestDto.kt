package com.hyu_oms.restapi.v5.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class AuthTokenInitialIssueRequestDto(
    @field:NotEmpty
    @JsonProperty("social_account_type")
    var socialAccountType: String?,

    @field:NotEmpty
    var code: String?,

    @field:NotEmpty
    @JsonProperty("redirected_url")
    var redirectedUrl: String?
)