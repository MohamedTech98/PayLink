package com.paylink.shared.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {
    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "application", "PayLink",
                "status", "running",
                "message", "PayLink backend foundation is ready"
        );
    }
}
