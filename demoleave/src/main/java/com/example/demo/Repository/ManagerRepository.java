package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainId;
import com.example.demo.Entity.ManagerLeaveApplication;

public interface ManagerRepository extends JpaRepository<ManagerLeaveApplication, Integer>, JpaSpecificationExecutor<ManagerLeaveApplication> {
}
