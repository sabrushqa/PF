package com.example.demo.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "6LdVOC8rAAAAAEvjYnPJEegjkfhEkFdS0AUjqIAy";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if ("/login".equals(request.getServletPath()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String recaptchaResponse = request.getParameter("g-recaptcha-response");

            if (!verifyCaptcha(recaptchaResponse)) {
                System.out.println("🛑 CAPTCHA invalide !");
                response.sendRedirect("/login?error=captcha");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean verifyCaptcha(String recaptchaResponse) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestBody = "secret=" + SECRET_KEY + "&response=" + recaptchaResponse;

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, request, Map.class);

            Map<String, Object> body = response.getBody();
            return (Boolean) body.get("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
