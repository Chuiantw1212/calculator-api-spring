package com.en_chu.calculator_api_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.en_chu.calculator_api_spring.service.FirebaseSeedingService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FirebaseSeedingService seedingService;

    @GetMapping("/sync-firebase")
    public String sync() {
        seedingService.syncAllConfigs();
        return "同步指令已發出，請查看後端 Log 紀錄。";
    }
}