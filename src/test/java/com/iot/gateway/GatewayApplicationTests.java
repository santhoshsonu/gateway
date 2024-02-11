package com.iot.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest
class GatewayApplicationTests {

  @Test
  void contextLoads() {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(GatewayApplicationTests.class);
    assertNotNull(context);
  }
}
