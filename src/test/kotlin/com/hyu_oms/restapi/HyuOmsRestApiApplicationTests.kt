package com.hyu_oms.restapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles(value = ["test"])
class HyuOmsRestApiApplicationTests {

  @Test
  fun contextLoads() {
  }

}
