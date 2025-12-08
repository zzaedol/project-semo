package com.semo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {

  @GetMapping(value = {"", "/"})
  public String main() {
    return "main/main";
  }

  @GetMapping("/signup")
  public String signup() {
    return "main/signup";
  }

  @GetMapping("/login")
  public String login() {
    return "main/login";
  }

  @GetMapping("/customer-service")
  public String customerService() {
    return "main/customer-service";
  }

  @GetMapping("/service-intro")
  public String serviceIntro() {
    return "main/service-intro";
  }

  @GetMapping("/pricing")
  public String pricing() {
    return "main/pricing";
  }

  @GetMapping("/location")
  public String location() {
    return "main/location-v2";
  }

}
