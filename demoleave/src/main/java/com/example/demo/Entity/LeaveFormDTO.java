package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveFormDTO {
    private Integer userId;
    private Integer year;
    private Integer annualRemain;
    private Integer sickRemain;
    private Integer compassionLeave;
    private Integer companyLeave;
    private Integer birthdayRemain;
    private Integer timeOffLeave;
    private Integer unpaidLeave;
    private Integer hospitalRemain;

    public LeaveFormDTO(Integer userId, Integer year, Integer annualRemain, Integer sickRemain, Integer compassionLeave,
    		Integer companyLeave,Integer birthdayRemain, Integer timeOffLeave,Integer unpaidLeave,Integer hospitalRemain) {
        this.userId = userId;
        this.year = year;
        this.annualRemain = annualRemain;
        this.sickRemain = sickRemain;
        this.compassionLeave = compassionLeave;
        this.companyLeave= companyLeave;
        this.birthdayRemain= birthdayRemain;
        this.timeOffLeave= timeOffLeave;
        this.unpaidLeave= unpaidLeave;
        this.hospitalRemain= hospitalRemain;
        
    }
}
