package com.example.demo.Entity;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user", schema = "leavedb")
public class User implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", nullable = false)
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "JobTitle", nullable = false)
    private String jobTitle;

    @Column(name = "Supervisor")
    private Integer supervisor;
    
    @Column(name = "YearsOfWork", nullable = false)
    private Integer yearsOfWork;
    
    @Column(name = "Age", nullable = false)
    private Integer age;
    
    @Column(name = "EmailAddress", nullable = false)
    private String emailAddress;
    
    @Column(name = "DateOfBirth", nullable = false)
    private Date dateOfBirth;
    
    @Column(name = "JoinDate", nullable = false)
    private Date joinDate;
}