package com.srjons.templatejpa.controller;

import com.srjons.templatejpa.entity.User;
import com.srjons.templatejpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable("userId") int userId) {
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUser(@PathVariable("userId") int userId) {
        return userService.deleteById(userId);
    }
}
