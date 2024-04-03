package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRemainDTO {
    private Integer userID;
    private String username;
    private Integer year;
    private Integer AnnualRemain;
    private Integer SickRemain;
    private Integer CompassionLeave;
    private Integer CompanyLeave;
    private Integer BirthdayRemain;
    private Integer TimeOffLeave;
    private Integer UnpaidLeave;
    private Integer HospitalRemain;

    public LeaveRemainDTO(Integer userID,String username, Integer year, Integer annualRemain, Integer sickRemain, Integer compassionLeave,
    		Integer companyLeave,Integer birthdayRemain, Integer timeOffLeave,Integer unpaidLeave,Integer hospitalRemain) {
        this.userID = userID;
        this.username = username;
        this.year = year;
        this.AnnualRemain = annualRemain;
        this.SickRemain = sickRemain;
        this.CompassionLeave = compassionLeave;
        this.CompanyLeave= companyLeave;
        this.BirthdayRemain= birthdayRemain;
        this.TimeOffLeave= timeOffLeave;
        this.UnpaidLeave= unpaidLeave;
        this.HospitalRemain= hospitalRemain;
        
        
    }
    @Override
    public String toString() {
        return "LeaveRemainDTO{" +
                "userID=" + userID +
                "username='" + username + '\'' +
                ", year=" + year +
                ", AnnualRemain=" + AnnualRemain +
                ", SickRemain=" + SickRemain +
                ", CompassionLeave=" + CompassionLeave +
                ", CompanyLeave=" + CompanyLeave +
                ", BirthdayRemain=" + BirthdayRemain +
                ", TimeOffLeave=" + TimeOffLeave +
                ", UnpaidLeave=" + UnpaidLeave +
                ", HospitalRemain=" + HospitalRemain +
                '}';
    }
}
