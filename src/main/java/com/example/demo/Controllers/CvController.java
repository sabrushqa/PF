package com.example.demo.Controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CvController {

    // Affiche la page du générateur de CV
    @GetMapping("/cv-generator")
    public String showCvGenerator() {
        return "cv-generator"; // correspond à cv-generator.html dans /templates
    }
}

