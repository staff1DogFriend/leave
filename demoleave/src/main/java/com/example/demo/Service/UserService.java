package com.example.demo.Service;
import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.LeaveApplicationRepository;
import com.example.demo.Repository.LeaveRemainRepository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.example.demo.Entity.User;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LeaveRemainRepository leaveRemainRepository;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;
    public User authenticateUser(Integer Id, String password) {
        return userRepository.findByIdAndPassword(Id, password);
    }

    public Page<User> searchUsers(String username, Integer userId, String jobTitle, Pageable pageable) {
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();
            if (username != null && !username.isEmpty()) {
                predicate.add(criteriaBuilder.equal(root.get("name"), username));
            }
            if (userId != null) {
                predicate.add(criteriaBuilder.equal(root.get("id"), userId));
            }
            if (jobTitle != null && !jobTitle.isEmpty()) {
                predicate.add(criteriaBuilder.equal(root.get("jobTitle"), jobTitle));
            }
            return criteriaBuilder.and(predicate.toArray(new Predicate[0]));
        };
        return userRepository.findAll(specification, pageable);
    }

    public void deleteUser(Integer userId) {
    	leaveRemainRepository.deleteByUserId(userId);
    	leaveApplicationRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        
    }

    public void addUser(User user){
        userRepository.save(user);
    }

    public User getUserById(Integer userId){
        return userRepository.findById(userId).orElse(null);
    }
    
    public User getUserByName(String userName){
        return userRepository.findByName(userName).orElse(null);
    }

    public void updateUser(User user){
        User user1 = userRepository.findById(user.getId()).orElse(null);
        user1.setName(user.getName());
        user1.setPassword(user.getPassword());
        user1.setJobTitle(user.getJobTitle());
        user1.setSupervisor(user.getSupervisor());
        user1.setEmailAddress(user.getEmailAddress());

        user1.setYearsOfWork(user.getYearsOfWork());
        user1.setDateOfBirth(user.getDateOfBirth());
        user1.setAge(user.getAge());
        user1.setJoinDate(user.getJoinDate());
        userRepository.save(user1);
    }

    public void submitLeaveApplication(LeaveApplication leaveApplication) {
        // Set additional fields before saving if needed
        leaveApplication.setStatus("Applied"); // Set default status to Pending
        leaveApplication.setSubmitDate(Instant.now().atZone(ZoneId.of("UTC")).plusHours(8).toInstant());
        // Set submit date to the current timestamp
        // Save the LeaveApplication entity to the database
        leaveApplicationRepository.save(leaveApplication);
    }
    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<User> getNotEmployeeUsers() {
        return userRepository.findByJobTitleNot("Employee");
    }
}



