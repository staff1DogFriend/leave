package com.example.demo.Repository;
import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.ManagerLeaveApplication;
import com.example.demo.Entity.ManagerLeaveApplicationDTO;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagerLeaveApplicationRepository extends JpaRepository<ManagerLeaveApplication, Integer> {
    Page<ManagerLeaveApplication> findByApproverID(Integer approverID
    		, Pageable pageable);
    Optional<ManagerLeaveApplication> findByApplicationId(Integer id);
    Page<ManagerLeaveApplication> findByApproverIDAndStatus(Integer managerId, String status, Pageable pageable);
    Page<ManagerLeaveApplication> findByApproverIDAndStatusNot(Integer managerId, String status, Pageable pageable);
    
    //for admin
    Page<ManagerLeaveApplication> findByStatus(String status, Pageable pageable);
    Page<ManagerLeaveApplication> findByStatusNot(String status, Pageable pageable);
    

    //admin
 // 根据状态、用户名（模糊匹配）、用户ID、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND m.user.id = :userId AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYearAndStartDateMonth(
            @Param("status") String status,
            @Param("username") String username,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据状态、用户名（模糊匹配）、用户ID和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND m.user.id = :userId AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYear(
            @Param("status") String status,
            @Param("username") String username,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            Pageable pageable);

    // 根据状态、用户名（模糊匹配）、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCaseAndStartDateYearAndStartDateMonth(
            @Param("status") String status,
            @Param("username") String username,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据状态、用户名（模糊匹配）、年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCaseAndStartDateYear(
            @Param("status") String status,
            @Param("username") String username,
            @Param("year") Integer year,
            Pageable pageable);

 // 根据状态、用户ID、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND m.user.id = :userId AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByStatusAndUser_IdAndStartDateYearAndStartDateMonth(
            @Param("status") String status,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据状态、用户ID和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND m.user.id = :userId AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByStatusAndUser_IdAndStartDateYear(
            @Param("status") String status,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            Pageable pageable);

    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND FUNCTION('YEAR', m.startDate) = :year AND FUNCTION('MONTH', m.startDate) = :month")
    Page<ManagerLeaveApplication> findByStatusAndYearAndMonth(
            @Param("status") String status,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);


    // 根据状态和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.status = :status AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByStatusAndStartDateYear(
            @Param("status") String status,
            @Param("year") Integer year,
            Pageable pageable);

    
    //manager
 // 根据审批者ID、状态、用户名、用户ID、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND m.user.id = :userId AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYearAndStartDateMonth(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("username") String username,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据审批者ID、状态、用户名、用户ID和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND m.user.id = :userId AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYear(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("username") String username,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            Pageable pageable);

    // 根据审批者ID、状态、用户名和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndStartDateYear(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("username") String username,
            @Param("year") Integer year,
            Pageable pageable);


 // 根据审批者ID、状态、用户名和年份yuefen进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND LOWER(m.user.name) LIKE LOWER(CONCAT('%', :username, '%')) AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndStartDateYearAndStartDateMonth(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("username") String username,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

 // 根据审批者ID、状态、用户ID、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND m.user.id = :userId AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_IdAndStartDateYearAndStartDateMonth(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据审批者ID、状态、用户ID和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND m.user.id = :userId AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_IdAndStartDateYear(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("userId") Integer userId,
            @Param("year") Integer year,
            Pageable pageable);

    // 根据审批者ID、状态、年份和月份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND YEAR(m.startDate) = :year AND MONTH(m.startDate) = :month")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndStartDateYearAndStartDateMonth(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable);

    // 根据审批者ID、状态和年份进行查询
    @Query("SELECT m FROM ManagerLeaveApplication m WHERE m.approverID = :managerId AND m.status = :status AND YEAR(m.startDate) = :year")
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndStartDateYear(
            @Param("managerId") Integer managerId,
            @Param("status") String status,
            @Param("year") Integer year,
            Pageable pageable);

    
    
    
    
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCaseAndUser_Id(String status, String username, Integer userId, Pageable pageable);

    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndUser_Id(Integer managerId ,String status, String username, Integer userId, Pageable pageable);
    // 按照状态和用户名模糊匹配进行查询
    Page<ManagerLeaveApplication> findByStatusAndUser_NameContainingIgnoreCase(String status, String username, Pageable pageable);

    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_NameContainingIgnoreCase(Integer managerId ,String status, String username, Pageable pageable);

    // 按照状态和用户ID进行查询
    Page<ManagerLeaveApplication> findByStatusAndUser_Id(String status, Integer userId, Pageable pageable);
    
    Page<ManagerLeaveApplication> findByApproverIDAndStatusAndUser_Id(Integer managerId ,String status, Integer userId, Pageable pageable);
}
