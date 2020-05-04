package com.hyu_oms.restapi.v5.responses

class ClientError4XX {
  companion object {
    val REST_CLIENT_ERROR = mutableMapOf<String, Any?>(
        "code" to "F000",
        "message" to "Error occurred from external API server.",
        "data" to null
    )
    val JWT_CREATION_ERROR = mutableMapOf<String, Any?>(
        "code" to "F001",
        "message" to "Unable to create new JWT.",
        "data" to null
    )
    val JWT_VERIFICATION_ERROR = mutableMapOf<String, Any?>(
        "code" to "F002",
        "message" to "Unable to verify given JWT.",
        "data" to null
    )
  }
}