package com.example.demo.Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.ManagerLeaveApplicationDTO;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainId;
import com.example.demo.Entity.ManagerLeaveApplication;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ManagerRepository;
@Service
@Transactional
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    public ManagerLeaveApplication operateLeave(ManagerLeaveApplicationDTO managerleaveapplicationDTO) {
        ManagerLeaveApplication newManagerLeaveapplication = new ManagerLeaveApplication();
        newManagerLeaveapplication.setApplicationId(managerleaveapplicationDTO.getApplicationId());  // 可以使用 setId 方法设置 LeaveApplication 的 id 字段
        newManagerLeaveapplication.setComment(managerleaveapplicationDTO.getComment());
        newManagerLeaveapplication.setStatus(managerleaveapplicationDTO.getStatus());
        return managerRepository.save(newManagerLeaveapplication);
    }

}




