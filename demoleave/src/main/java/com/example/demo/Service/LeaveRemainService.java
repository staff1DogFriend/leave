package com.example.demo.Service;

import com.example.demo.Entity.LeaveRemainId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Repository.LeaveRemainRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.Entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
public class LeaveRemainService {
    @Autowired
    private LeaveRemainRepository leaveRemainRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<LeaveRemainDTO> GetAllLeaveRemains(Pageable pageable) {
        Pageable SortByUserAndYear = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id.userID").and(Sort.by("id.year")));
        Page<LeaveRemain> leaveRemainPage = leaveRemainRepository.findAll(SortByUserAndYear);
        return leaveRemainPage.map(this::convertToLeaveRemainDTO);
    }

    public void updateLeaveRemain(LeaveRemainDTO leaveRemainDTO) {
        LeaveRemainId leaveRemainId = new LeaveRemainId(leaveRemainDTO.getUserID(), leaveRemainDTO.getYear());
        LeaveRemain leaveRemain = leaveRemainRepository.findById(leaveRemainId).orElse(null);
        leaveRemain.setAnnualRemain(leaveRemainDTO.getAnnualRemain());
        leaveRemain.setSickRemain(leaveRemainDTO.getSickRemain());
        leaveRemain.setCompassionLeave(leaveRemainDTO.getCompassionLeave());
        
        leaveRemain.setCompanyLeave(leaveRemainDTO.getCompanyLeave());
        leaveRemain.setBirthdayRemain(leaveRemainDTO.getBirthdayRemain());
        leaveRemain.setTimeOffLeave(leaveRemainDTO.getTimeOffLeave());
        leaveRemain.setUnpaidLeave(leaveRemainDTO.getUnpaidLeave());
        leaveRemain.setHospitalRemain(leaveRemainDTO.getHospitalRemain());
        leaveRemainRepository.save(leaveRemain);
    }

    public LeaveRemainDTO getLeaveRemainByUserIDAndYear(Integer userId, Integer year) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);
        LeaveRemain leaveRemain = leaveRemainRepository.findById(leaveRemainId).orElse(null);
        if (leaveRemain != null) {
            return convertToLeaveRemainDTO(leaveRemain);
        } else {
            return null;
        }
    }

    public boolean checkIfRecordExists(Integer userId, Integer year) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);
        return leaveRemainRepository.findById(leaveRemainId).isPresent();
    }

    private LeaveRemainDTO convertToLeaveRemainDTO(LeaveRemain leaveRemain) {
        User user = userRepository.findById(leaveRemain.getId().getUserID()).orElse(null);
        if (user != null) {
            return new LeaveRemainDTO(
                    user.getId(),
                    user.getName(),
                    leaveRemain.getId().getYear(),
                    leaveRemain.getAnnualRemain(),
                    leaveRemain.getSickRemain(),
                    leaveRemain.getCompassionLeave(),
                    leaveRemain.getCompanyLeave(),
                    leaveRemain.getBirthdayRemain(),
                    leaveRemain.getTimeOffLeave(),
                    leaveRemain.getUnpaidLeave(),
                    leaveRemain.getHospitalRemain()
            );
        } else {
            return null;
        }
    }

    public void deleteLeave(Integer userId, Integer year) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);
        leaveRemainRepository.deleteById(leaveRemainId);
    }

    public Page<LeaveRemainDTO> searchLeave(String username, Integer userId, Integer year, Pageable pageable) {
        Specification<LeaveRemain> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("user").get("name"), username));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("id").get("year"), year));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<LeaveRemain> leaveRemainPage = leaveRemainRepository.findAll(spec, pageable);
        return leaveRemainPage.map(this::convertToLeaveRemainDTO);
    }
}
