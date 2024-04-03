package com.example.demo.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Entity.LeaveApplication;

import java.time.LocalDate;
import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication,Integer>{
	List<LeaveApplication> findByUser_IdAndStatus(Integer UserID, String Status);
	List<LeaveApplication> findByUser_Id(Integer UserID);
	Page<LeaveApplication> findAllByStartDateBetweenAndStatus(LocalDate start, LocalDate end, String status, Pageable pageable);
	void deleteByUserId(Integer userId);
}
