package com.example.demo.Repository;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LeaveRemainRepository extends JpaRepository<LeaveRemain, LeaveRemainId>, JpaSpecificationExecutor<LeaveRemain> {
	void deleteByUserId(Integer userId);
}
