package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainId;

public interface AdminRepository extends JpaRepository<LeaveRemain,LeaveRemainId>{}
