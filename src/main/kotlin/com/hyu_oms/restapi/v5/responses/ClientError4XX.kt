package com.hyu_oms.restapi.v5.responses

class ClientError4XX {
  companion object {
    val REST_CLIENT_ERROR = mutableMapOf<String, Any?>(
        "code" to "F000",
        "message" to "Error occurred from external API server.",
        "data" to null
    )
  }
}