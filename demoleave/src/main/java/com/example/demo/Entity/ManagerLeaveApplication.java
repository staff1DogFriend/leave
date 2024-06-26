package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "leave_application", schema = "leavedb")
public class ManagerLeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ApplicationID", nullable = false)
    private Integer applicationId;
    
    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "UserID", nullable = false)
    private User user;
    
    @Transient
    private String userName;
    
    @Column(name = "LeaveType", nullable = false, length = 50)
    private String leaveType;

    @Column(name = "Reason", length = 500)
    private String reason;

    @Column(name = "StartDate", nullable = false)
    private Date startDate;

    @Column(name = "EndDate", nullable = false)
    private Date endDate;

    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Column(name = "SubmitDate", nullable = false)
    private LocalDateTime submitDate;

    @Column(name = "ApproverID")
    private Integer approverID;

    @Column(name = "ApproveDate")
    private LocalDateTime approveDate;

    @Column(name = "Comment", length = 500)
    private String comment;
    

    //@ManyToOne
    //@JoinColumn(name = "UserID", referencedColumnName = "UserId", nullable = false)
    //private User user;
}