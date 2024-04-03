package com.example.demo.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.demo.Service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Service.EmailService;
import com.example.demo.Service.LeaveRemainService;
import com.example.demo.Service.UserService;
import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class LeaveSubmissionController {
    @Autowired
    UserService userService;
    @Autowired
    LeaveRemainService leaveremainService;
    @Autowired
    SubmissionService submissionService;
    @Autowired
    private EmailService emailService;

    @GetMapping("{jobTitle}/{userID}/leaveSubmission")
    public String showLeaveForm(@PathVariable String jobTitle, @PathVariable String userID, HttpSession session) {
        User user = (User) session.getAttribute("user");
        return "leaveSubmission";
    }

    @PostMapping("{jobTitle}/{userID}/submitLeave")
    public String submitLeave(
            @PathVariable String jobTitle,
            @PathVariable String userID,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String leaveType,
            @RequestParam String reason,
            HttpSession session, Model model
    ) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("userId", user.getId());
        Integer userId = user.getId();
        String userName = user.getName();
        String s=jobTitle+"Welcome";
        System.out.print(startDate.getMonth()+"   ");
        
        if ((leaveType.equals("Annual")&&(startDate.getMonthValue() == 1)&&(!(user.getJoinDate().getYear()==LocalDate.now().getYear())))) {
            // 如果是一月份而且是老员工，首先考虑使用前一年的数据

                System.out.print(1);
                //for a user, if there's already application for vacation
                //(means the status is "Applied"),
                //then user cannot apply for vacation until status is changed.
                boolean applicationOK = false;
                int year = startDate.getYear()-1;
                Integer daysDifference = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
                daysDifference = SubmissionService.countWorkingDays(startDate, endDate);
                //Check user's application
                //1.check select time:
                //2.check whether time is enough, including compensation time.
                System.out.print("  checkTimeOverlapping"+submissionService.checkTimeOverlapping(userId, startDate, endDate));
                
                System.out.print("  checkTime"+submissionService.checkTime(startDate, endDate));
                
                System.out.print("  checkAvailability"+submissionService.checkAvailability(userId,startDate, year, leaveType, daysDifference));
                
                if (!submissionService.checkTimeOverlapping(userId, startDate, endDate) && submissionService.checkTime(startDate, endDate) && submissionService.checkAvailability(userId,startDate, year, leaveType, daysDifference)) {
                	applicationOK = true;
                    
                	System.out.print("  application"+applicationOK);
                }
                //if checking is done, add this application to our database.
                if (applicationOK) {
                    LeaveApplication newLeave = new LeaveApplication();
                    newLeave.setLeaveType(leaveType);
                    newLeave.setStartDate(startDate);
                    newLeave.setEndDate(endDate);
                    newLeave.setUser(user);
                    newLeave.setReason(reason);
                    newLeave.setApproverID(user.getSupervisor());
                    userService.submitLeaveApplication(newLeave);
                    LeaveRemainDTO toEditLeave = leaveremainService.getLeaveRemainByUserIDAndYear(userId, year);
                    Integer annualRemain = toEditLeave.getAnnualRemain();
                    Integer sickRemain = toEditLeave.getSickRemain();
                    Integer compassionLeave = toEditLeave.getCompassionLeave();
                    Integer companyLeave= toEditLeave.getCompanyLeave();
                    Integer birthdayRemain= toEditLeave.getBirthdayRemain();
                    Integer timeOffLeave= toEditLeave.getTimeOffLeave();
                    Integer unpaidLeave= toEditLeave.getUnpaidLeave();
                    Integer hospitalRemain= toEditLeave.getHospitalRemain();
                    
                    
                    
                    //When apply annual vacation, we need to check everyday in apply : if the leave period is <= 14 calendar days, weekends / public holidays are excluded.
                    //Otherwise, weekends / public holidays are included
                    //1.weekend?
                    //2.public holiday?
                    

                    LeaveRemainDTO updateRemain = submissionService.countRemain(userId, userName, year, daysDifference , annualRemain , sickRemain , compassionLeave , companyLeave , birthdayRemain , timeOffLeave , unpaidLeave , hospitalRemain , leaveType);
                    leaveremainService.updateLeaveRemain(updateRemain);
                    
                    
                    emailService.sendLeaveApplicationEmail(userID, leaveType);
                    
                    model.addAttribute("ApplicationStatus", "Your application has been submitted successfully.");
                }
                //这里出现问题，如果判断结果不是OK，则考虑另一种解决方法
                //使用当前年的数据和往年数据同时
                //刚刚已经判断了往年一年的annual remain不足，但是只要加上今年的还够够用，也完全没有问题
                //所以需要重新考虑另一种计算方式
                //添加一种else if，用来重新考虑，如果applicationok对于两年的remain数据可行，也可以实现请假
            else
            {
            	if (!submissionService.checkTimeOverlapping(userId, startDate, endDate) && 
            			submissionService.checkTime(startDate, endDate) && 
            			!submissionService.checkAvailability(userId,startDate, year, leaveType, daysDifference) &&
            			submissionService.checkAvailabilityForTwoYears(userId,startDate, year,year+1, leaveType, daysDifference))
            	{
                	
            		LeaveApplication newLeave = new LeaveApplication();
                    newLeave.setLeaveType(leaveType);
                    newLeave.setStartDate(startDate);
                    newLeave.setEndDate(endDate);
                    newLeave.setUser(user);
                    newLeave.setReason(reason);
                    newLeave.setApproverID(user.getSupervisor());
                    userService.submitLeaveApplication(newLeave);
                    LeaveRemainDTO toEditLeave = leaveremainService.getLeaveRemainByUserIDAndYear(userId, year);
                    Integer annualRemain = toEditLeave.getAnnualRemain();
                    if(annualRemain>5)
                    	annualRemain=5;

                    Integer daysDifference2=daysDifference-annualRemain;
                    LeaveRemainDTO toEditLeave2 = leaveremainService.getLeaveRemainByUserIDAndYear(userId, year+1);
                    Integer annualRemain2 = toEditLeave2.getAnnualRemain();
                    Integer sickRemain2 = toEditLeave2.getSickRemain();
                    Integer compassionLeave2 = toEditLeave2.getCompassionLeave();
                    Integer companyLeave2= toEditLeave2.getCompanyLeave();
                    Integer birthdayRemain2= toEditLeave2.getBirthdayRemain();
                    Integer timeOffLeave2= toEditLeave2.getTimeOffLeave();
                    Integer unpaidLeave2= toEditLeave2.getUnpaidLeave();
                    Integer hospitalRemain2= toEditLeave2.getHospitalRemain();
                    
                    LeaveRemainDTO updateRemain2 = submissionService.countRemain(userId, userName, year+1, daysDifference2 , annualRemain2 , sickRemain2 , compassionLeave2 , companyLeave2 , birthdayRemain2 , timeOffLeave2 , unpaidLeave2 , hospitalRemain2 , leaveType);
                    
                    if(toEditLeave.getAnnualRemain()>5)
                    	toEditLeave.setAnnualRemain(toEditLeave.getAnnualRemain()-5);
                    else
                    	toEditLeave.setAnnualRemain(0);
                    leaveremainService.updateLeaveRemain(toEditLeave);
                    leaveremainService.updateLeaveRemain(updateRemain2);
                    
                    emailService.sendLeaveApplicationEmail(userID, leaveType);
                    
                    model.addAttribute("ApplicationStatus", "Your application has been submitted successfully.");
                	
                }
            	
            	

            	else {
        			model.addAttribute("ApplicationStatus", "Please check your application's start and end date.");
                }
            }
        }
        
        else {
        //for a user, if there's already application for vacation
        //(means the status is "Applied"),
        //then user cannot apply for vacation until status is changed.
        boolean applicationOK = false;
        int year = startDate.getYear();
        Integer daysDifference = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        daysDifference = SubmissionService.countWorkingDays(startDate, endDate);
        //Check user's application
        //1.check select time:
        //2.check whether time is enough, including compensation time.
        
        System.out.print("  checkTimeOverlapping"+submissionService.checkTimeOverlapping(userId, startDate, endDate));
        
        System.out.print("  checkTime"+submissionService.checkTime(startDate, endDate));
        
        System.out.print("  checkAvailability"+submissionService.checkAvailability(userId,startDate, year, leaveType, daysDifference));
        
        
        if (!submissionService.checkTimeOverlapping(userId, startDate, endDate) && submissionService.checkTime(startDate, endDate) && submissionService.checkAvailability(userId,startDate, year, leaveType, daysDifference)) {
        	applicationOK = true;
        	System.out.print("  application"+applicationOK);
        }
        //if checking is done, add this application to our database.
        if (applicationOK) {
            LeaveApplication newLeave = new LeaveApplication();
            newLeave.setLeaveType(leaveType);
            newLeave.setStartDate(startDate);
            newLeave.setEndDate(endDate);
            newLeave.setUser(user);
            newLeave.setReason(reason);
            newLeave.setApproverID(user.getSupervisor());
            userService.submitLeaveApplication(newLeave);
            LeaveRemainDTO toEditLeave = leaveremainService.getLeaveRemainByUserIDAndYear(userId, year);
            Integer annualRemain = toEditLeave.getAnnualRemain();
            Integer sickRemain = toEditLeave.getSickRemain();
            Integer compassionLeave = toEditLeave.getCompassionLeave();
            Integer companyLeave= toEditLeave.getCompanyLeave();
            Integer birthdayRemain= toEditLeave.getBirthdayRemain();
            Integer timeOffLeave= toEditLeave.getTimeOffLeave();
            Integer unpaidLeave= toEditLeave.getUnpaidLeave();
            Integer hospitalRemain= toEditLeave.getHospitalRemain();
            
            
            
            //When apply annual vacation, we need to check everyday in apply : if the leave period is <= 14 calendar days, weekends / public holidays are excluded.
            //Otherwise, weekends / public holidays are included
            //1.weekend?
            //2.public holiday?
            
            
            daysDifference = SubmissionService.countWorkingDays(startDate, endDate);
            LeaveRemainDTO updateRemain = submissionService.countRemain(userId, userName, year, daysDifference , annualRemain , sickRemain , compassionLeave , companyLeave , birthdayRemain , timeOffLeave , unpaidLeave , hospitalRemain , leaveType);
            leaveremainService.updateLeaveRemain(updateRemain);
            
            
            emailService.sendLeaveApplicationEmail(userID, leaveType);
            
            model.addAttribute("ApplicationStatus", "Your application has been submitted successfully.");
        } else {
			model.addAttribute("ApplicationStatus", "Please check your application's start and end date.");
        	}

        }
        
        return s;

    }

}