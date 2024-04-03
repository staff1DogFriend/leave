package com.example.demo.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String officialEmail;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendEmail(String adminEmail, String userID) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(officialEmail);
        msg.setTo(adminEmail);
        msg.setSubject("Password Reset Request");
        msg.setText("User with ID " + userID + " has requested to reset password. Please reset password for this user.");
        javaMailSender.send(msg);
    }
    
    public void sendLeaveApplicationEmail(String userID,String leaveType) {
    	
    	User user1 = userRepository.findById(Integer.parseInt(userID)).orElse(null);
    	
    	User user2 = userRepository.findById(user1.getSupervisor()).orElse(null);
    	System.out.print(user2.getEmailAddress());
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(officialEmail);
        msg.setTo(user2.getEmailAddress());
        msg.setSubject("Leave Application");
        msg.setText("User with ID " + userID + " has requested to take one " + leaveType + " leave application. Please operate on the system for this user.");
        javaMailSender.send(msg);
    }
    
    public void sendOperateLeaveApplicationEmail(Integer userID,String leaveType,String status) {
    	
    	User user1 = userRepository.findById(userID).orElse(null);
    	
    	System.out.print(user1.getEmailAddress());
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(officialEmail);
        msg.setTo(user1.getEmailAddress());
        msg.setSubject("Leave Application");
        msg.setText("Your " + leaveType + " has been " + status + ". Please go to the system and check it.");
        javaMailSender.send(msg);
    }
    
    public void sendCancelEmail(Integer userID,String leaveType,String startDate,String endDate) {
    	
    	User user1 = userRepository.findById(userID).orElse(null);
    	
    	User user2 = userRepository.findById(user1.getSupervisor()).orElse(null);
    	System.out.print(user2.getEmailAddress());
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(officialEmail);
        msg.setTo(user2.getEmailAddress());
        msg.setSubject("Cancel Approved Application");
        msg.setText("User with ID " + userID + " wants to cancel the APPROVED leave application. The leave is in " + leaveType + " type and lasts from "+startDate+" to "+endDate+" . Please go to the system and check it.");
        javaMailSender.send(msg);
    }
}
