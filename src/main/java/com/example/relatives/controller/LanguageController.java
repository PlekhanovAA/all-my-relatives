package com.example.relatives.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Controller
public class LanguageController {

    @GetMapping("/change-lang")
    public String changeLanguage(@RequestParam String lang, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("?")) {
            referer += "&lang=" + lang;
        } else if (referer != null) {
            referer += "?lang=" + lang;
        }
        return "redirect:" + (referer != null ? referer : "/");
    }
}
