/**
 * A Health Check Path for external health checker like K8s.
 *
 * @author hoony9x <me@hoony9x.com>
 */

package com.hyu_oms.restapi.v5.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v5/health-check")
class HealthCheckController {
  /**
   * Returns API version and random UUID string.
   *
   * @return {"version": "5.x", "uuid": "RANDOM_UUID_STRING"}
   */
  @GetMapping
  fun healthCheck(): Map<String, String> {
    return mapOf("version" to "5.x", "uuid" to UUID.randomUUID().toString())
  }
}