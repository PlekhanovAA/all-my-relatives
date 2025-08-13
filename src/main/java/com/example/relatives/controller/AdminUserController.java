package com.example.relatives.controller;

import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model, Principal principal) {
        User owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));
        List<User> users = userRepository.findByOwner(owner);
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    @GetMapping("/invite")
    public String inviteForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/invite_user";
    }

    @PostMapping("/invite")
    public String inviteUser(@ModelAttribute User user, Principal principal) {
        User owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.VIEWER); // 👈 здесь всегда VIEWER
        user.setOwner(owner);
        userRepository.save(user);
        return "redirect:/admin/users";
    }
}
