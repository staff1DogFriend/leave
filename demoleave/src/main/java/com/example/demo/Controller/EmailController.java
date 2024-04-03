package com.example.demo.Controller;

import com.example.demo.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam String userID) {
        String adminEmail = "e1221773@u.nus.edu";
        emailService.sendEmail(adminEmail, userID);
        return ResponseEntity.ok("Password reset email sent to admin successfully!");
    }
    
    
    
}
