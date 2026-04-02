package com.example.poidetection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.poidetection.entity.User;
import com.example.poidetection.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        userRepository.save(user);
        return "User added successfully";
    }

    // NEW: Enable tracking (CONSENT)
    @PostMapping("/enable-tracking/{id}")
    public String enableTracking(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLocationTrackingEnabled(true);
        userRepository.save(user);

        return "Tracking enabled ";
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}