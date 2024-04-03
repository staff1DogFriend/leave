package com.example.demo.Service;
import jakarta.transaction.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainId;
import com.example.demo.Entity.User;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.UserRepository;
@Service
@Transactional
public class AdminService {
	@Autowired
    private UserService userService;
	
	
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    //using leave remain entity
    public LeaveRemain addLeave(Integer userId, Integer year, Integer AnnualRemain , Integer SickRemain , Integer CompassionLeave , 
    		Integer CompanyLeave , Integer BirthdayRemain ,Integer TimeOffLeave , Integer UnpaidLeave , Integer HospitalRemain) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);

        LeaveRemain newLeaveRemain = new LeaveRemain();
        newLeaveRemain.setId(leaveRemainId);
        newLeaveRemain.setAnnualRemain(AnnualRemain);
        newLeaveRemain.setSickRemain(SickRemain);
        newLeaveRemain.setCompassionLeave(CompassionLeave);
        newLeaveRemain.setCompanyLeave(CompanyLeave);
        newLeaveRemain.setBirthdayRemain(BirthdayRemain);
        newLeaveRemain.setTimeOffLeave(TimeOffLeave);
        newLeaveRemain.setUnpaidLeave(UnpaidLeave);
        newLeaveRemain.setHospitalRemain(HospitalRemain);

        return adminRepository.save(newLeaveRemain);
    }
    
    //used leave remain entity
    public LeaveRemain AutoAddLeaveRemain(Integer userId, Integer year) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);
        
        User user=userRepository.findById(userId).orElse(null);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getJoinDate());
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH)+1;
        
        Calendar calendar_now = Calendar.getInstance();
        int year2=calendar_now.get(Calendar.YEAR);
        
        int yearDifference = year2 - year1;
        System.out.print(yearDifference);
        //calculate annualRemain
        int AnnualRemain = 14;

        if (yearDifference == 0) {
            AnnualRemain = 14 * (12 - month1) / 12;
        }

        if (yearDifference >= 3) {
            AnnualRemain += yearDifference - 2;
        }

        if (AnnualRemain > 21) {
            AnnualRemain = 21;
        }

        
		//set other leaveremain
		int SickRemain=14;
		
		int CompassionLeave=0;
		
		int CompanyLeave=0;
		
		int BirthdayRemain=1;
		
		int TimeOffLeave=0;
		
		int UnpaidLeave=0;
		
		int HospitalRemain=46;
		
		
        LeaveRemain newLeaveRemain = new LeaveRemain();
        newLeaveRemain.setId(leaveRemainId);
        newLeaveRemain.setAnnualRemain(AnnualRemain);
        newLeaveRemain.setSickRemain(SickRemain);
        newLeaveRemain.setCompassionLeave(CompassionLeave);
        newLeaveRemain.setCompanyLeave(CompanyLeave);
        newLeaveRemain.setBirthdayRemain(BirthdayRemain);
        newLeaveRemain.setTimeOffLeave(TimeOffLeave);
        newLeaveRemain.setUnpaidLeave(UnpaidLeave);
        newLeaveRemain.setHospitalRemain(HospitalRemain);

        return adminRepository.save(newLeaveRemain);
    }
    
    public LeaveRemain AutoAddLeaveRemainForOneYear(Integer userId, Integer year) {
        LeaveRemainId leaveRemainId = new LeaveRemainId();
        leaveRemainId.setUserID(userId);
        leaveRemainId.setYear(year);
        
        User user=userRepository.findById(userId).orElse(null);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getJoinDate());
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH)+1;
        
        Calendar calendar_now = Calendar.getInstance();
        int year2=calendar_now.get(Calendar.YEAR);
        
        int yearDifference = year - year1;
        System.out.print(yearDifference);
        //calculate annualRemain
        int AnnualRemain = 14;

        if (yearDifference == 0) {
            AnnualRemain = 14 * (12 - month1) / 12;
        }

        if (yearDifference >= 3) {
            AnnualRemain += yearDifference - 2;
        }

        if (AnnualRemain > 21) {
            AnnualRemain = 21;
        }

        
		//set other leaveremain
		int SickRemain=14;
		
		int CompassionLeave=0;
		
		int CompanyLeave=0;
		
		int BirthdayRemain=1;
		
		int TimeOffLeave=0;
		
		int UnpaidLeave=0;
		
		int HospitalRemain=46;
		
		
        LeaveRemain newLeaveRemain = new LeaveRemain();
        newLeaveRemain.setId(leaveRemainId);
        newLeaveRemain.setAnnualRemain(AnnualRemain);
        newLeaveRemain.setSickRemain(SickRemain);
        newLeaveRemain.setCompassionLeave(CompassionLeave);
        newLeaveRemain.setCompanyLeave(CompanyLeave);
        newLeaveRemain.setBirthdayRemain(BirthdayRemain);
        newLeaveRemain.setTimeOffLeave(TimeOffLeave);
        newLeaveRemain.setUnpaidLeave(UnpaidLeave);
        newLeaveRemain.setHospitalRemain(HospitalRemain);

        return adminRepository.save(newLeaveRemain);
    }

}




