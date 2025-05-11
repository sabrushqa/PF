package com.example.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {

    @GetMapping("/")
    public String afficherAccueil() {
        return "index"; // Correspond à src/main/resources/templates/index.html
    }
}
