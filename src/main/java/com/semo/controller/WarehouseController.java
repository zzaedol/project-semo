package com.semo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController {

  @GetMapping("/register")
  public String registerPage() {
    log.info("창고 등록 페이지 요청");
    return "main/warehouse-register";
  }
}