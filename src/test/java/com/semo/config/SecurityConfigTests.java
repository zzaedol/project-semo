package com.semo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SecurityConfigTests {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void passwordEncoder() {
    System.out.println("argon2: " + passwordEncoder.encode("1234"));
  }
}