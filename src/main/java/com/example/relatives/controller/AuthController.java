package com.example.relatives.controller;

import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthController(UserRepository userRepo,
                          BCryptPasswordEncoder encoder,
                          @Lazy AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Имя пользователя уже занято.");
            return "register";
        }

        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(Role.ADMIN); // 👈 Явно задаём роль
        userRepo.save(user);

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/gallery";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Неверный логин или пароль");
        if (logout != null) model.addAttribute("message", "Вы вышли из системы");
        return "login";
    }
}
