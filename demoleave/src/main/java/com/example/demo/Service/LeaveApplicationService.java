package com.example.demo.Service;

import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.LeaveApplicationDTO;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Entity.User;
import com.example.demo.Repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.demo.Repository.UserRepository;

import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {
    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    public List<LeaveApplication> findByUser_Id(Integer id) {
        return leaveApplicationRepository.findByUser_Id(id);
    }
    public void deleteLeaveApplication(Integer id) {
        leaveApplicationRepository.deleteById(id);
    }

    public Page<LeaveApplicationDTO> getLeaveApplicationByMonth(Integer year, Integer month, Pageable pageable) {
        LocalDate startOfMonth= LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        Page<LeaveApplication> leaveApplicationPage = leaveApplicationRepository.findAllByStartDateBetweenAndStatus(startOfMonth, endOfMonth, "Approved", pageable);
        return leaveApplicationPage.map(this::convertToDTO);
    }

    public List<LeaveApplicationDTO> getLeaveApplicationForUser(Integer userId) {
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findByUser_Id(userId);
        return leaveApplications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    private LeaveApplicationDTO convertToDTO(LeaveApplication leaveApplication){
        LeaveApplicationDTO leaveApplicationDTO = new LeaveApplicationDTO();
        leaveApplicationDTO.setId(leaveApplication.getId());
        leaveApplicationDTO.setUsername(leaveApplication.getUser().getName());
        leaveApplicationDTO.setLeaveType(leaveApplication.getLeaveType());
        leaveApplicationDTO.setStartDate(formatLocalDate(leaveApplication.getStartDate()));
        leaveApplicationDTO.setEndDate(formatLocalDate(leaveApplication.getEndDate()));
        leaveApplicationDTO.setSubmitDate(formatInstant(leaveApplication.getSubmitDate()));
        leaveApplicationDTO.setReason(leaveApplication.getReason());
        leaveApplicationDTO.setStatus(leaveApplication.getStatus());
        leaveApplicationDTO.setApproverID(leaveApplication.getApproverID()==null?null:leaveApplication.getApproverID());
        leaveApplicationDTO.setApproveDate(formatInstant(leaveApplication.getApproveDate())==null?null:formatInstant(leaveApplication.getApproveDate()));
        leaveApplicationDTO.setComment(leaveApplication.getComment()==null?null:leaveApplication.getComment());
        if (leaveApplication.getApproverID() == null) {
            leaveApplicationDTO.setApproverName(null);
        } else {
            leaveApplicationDTO.setApproverName(userRepository.findById(leaveApplication.getApproverID()).map(User::getName).orElse(null));
        }
        return leaveApplicationDTO;
    }

    private String formatLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return localDate.format(formatter);
        }
    }

    private String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        } else {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        }
    }
    


}
