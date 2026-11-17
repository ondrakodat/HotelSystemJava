package com.kodat.complexhotelsystem.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actuator")
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<Void> checkHealth(){
        return ResponseEntity.ok().build();
    }

}
