package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateReport {

    private final AppMainDemoProject demoProject;

    public CreateReport(AppMainDemoProject demoProject) {
        this.demoProject = demoProject;
    }

    @GetMapping("/employees")
    String all() {
        demoProject.start();
        return "OK";
    }
}
