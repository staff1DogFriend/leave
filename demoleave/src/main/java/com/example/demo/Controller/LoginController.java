package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;

@SessionAttributes("user")
@Controller
public class LoginController {
    @Autowired
    UserService userservice;

    @GetMapping(value = {"/index", "/login"})
    public String show() {
        return "login";
    }

    @PostMapping("/login")
    public String login(String UserID, String password, HttpSession session, Model model) {
        try {
            Integer id = Integer.parseInt(UserID);
            User user = userservice.authenticateUser(id, password);
            if (user != null) {
                session.setAttribute("user", user);
                String jobTitle = user.getJobTitle();
                if ("Manager".equals(jobTitle)) {
                    return "redirect:/Manager";
                } else if ("Admin".equals(jobTitle)) {
                    return "redirect:/Admin";
                } else if ("Employee".equals(jobTitle)) {
                    return "redirect:/Employee";
                } else {
                    return "success";
                }
            } else {
                model.addAttribute("loginError", "Password is not valid or User does not exist");
                return "login";
            }
        } catch (NumberFormatException e) {
            model.addAttribute("loginError", "User ID is not valid");
            return "login";
        }

    }

}
