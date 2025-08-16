package com.example.relatives.controller;

import com.example.relatives.model.Role;
import com.example.relatives.model.User;
import com.example.relatives.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                .orElseThrow(() -> new RuntimeException("Владелец не найден"));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.VIEWER);
        user.setOwner(owner);

        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @PostMapping("/delete")
    @Transactional
    public String deleteUser(@RequestParam("username") String username,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        // Запрещаем удалять самого себя
        if (principal.getName().equals(username)) {
            redirectAttributes.addFlashAttribute("error", "Нельзя удалить самого себя");
            return "redirect:/admin/users";
        }

        userRepository.deleteByUsername(username);
        redirectAttributes.addFlashAttribute("success", "Пользователь удалён");
        return "redirect:/admin/users";
    }

}
