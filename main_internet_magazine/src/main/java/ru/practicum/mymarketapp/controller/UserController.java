package ru.practicum.mymarketapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.mymarketapp.entity.User;
import ru.practicum.mymarketapp.pojo.FormUser;
import ru.practicum.mymarketapp.service.UserService;


@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser( @ModelAttribute FormUser formUser) {
        userService.save(new User(null,formUser.username(),formUser.password())).subscribe();
        return "redirect:/login";
    }
}
