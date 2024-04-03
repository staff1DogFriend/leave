package com.example.demo.Service;

import com.example.demo.Entity.LeaveApplication;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Entity.User;
import com.example.demo.Repository.LeaveApplicationRepository;
import com.example.demo.Repository.LeaveRemainRepository;
import com.example.demo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionService {
    @Autowired
    private LeaveRemainService leaveRemainService;
    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;
    @Autowired
    private UserRepository userRepository;


    public boolean checkTimeOverlapping(Integer UserID, LocalDate startDate, LocalDate endDate) {
        List<LeaveApplication> oldApplications = leaveApplicationRepository.findByUser_IdAndStatus(UserID, "Applied");
        if (!oldApplications.isEmpty()) {
            for (LeaveApplication oldApplication : oldApplications) {
                if (!oldApplication.getStartDate().isAfter(endDate) && !oldApplication.getEndDate().isBefore(startDate)) {
                    return true;
                }
                ;
            }
        }   
        return false;
    }

    public boolean checkTime(LocalDate startDate, LocalDate endDate) {

        return ((isWorkingDay(startDate) && isWorkingDay(endDate)) && ((startDate.isBefore(endDate)) || startDate.isEqual(endDate))) && (startDate.getYear() == endDate.getYear());
        //if startDate is later than endDate,
        //this statement will be more than 0,return false.
        //And the vacation should be in one year, if start and end year is not equal, it will return false.
    }
 
    public boolean checkAvailability(Integer userId, LocalDate startDate, Integer Year, String leaveType, Integer daysDifference) {
        Integer leaveTime = 0;
        User usertest=userRepository.getById(userId);
        LeaveRemainDTO newLeave = leaveRemainService.getLeaveRemainByUserIDAndYear(userId, Year);
        //System.out.print("   "+usertest.getJoinDate().getYear()+"   "+startDate.getYear()+"   "+startDate.getMonth()+"   "+"Annual".equals(leaveType));
        if ((usertest.getJoinDate().getYear()+1900)==startDate.getYear() && startDate.getMonth() == Month.JANUARY && "Annual".equals(leaveType)) {
            return false; // 如果在新员工刚刚加入一年内的一月份请年假，则返回false
        }
        switch (leaveType) {
            case "Annual":
//                System.out.println("Annual");
                leaveTime = newLeave.getAnnualRemain();

                break;
            case "Sick":
//                System.out.println("Sick");
                leaveTime = newLeave.getSickRemain();
                break;
            case "Compassion":
//                System.out.println("Compassion");
                {leaveTime = newLeave.getCompassionLeave();
                return true;}
            case "Company":
//              System.out.println("Company");
              {leaveTime = newLeave.getCompanyLeave();
              return true;}
            case "Birthday":
//              System.out.println("Birthday");
              leaveTime = newLeave.getBirthdayRemain();
              break;
            case "TimeOff":
//              System.out.println("TimeOff");
              {
               leaveTime = newLeave.getTimeOffLeave();
              return true;
              }
            case "Unpaid":
//              System.out.println("Unpaid");
              {leaveTime = newLeave.getUnpaidLeave();
              return true;
              }
            case "Hospital":
//              System.out.println("Hospital");
              leaveTime = newLeave.getHospitalRemain();
              break;
            
                
                
            default:
//                System.out.println("useless");
        }
        LocalDate joinDate = usertest.getJoinDate().toLocalDate();

        if("Annual".equals(leaveType) && (usertest.getJoinDate().getYear()+1900)==startDate.getYear() && (joinDate.getMonthValue() <= startDate.getMonthValue()))
        {
	        Integer x=startDate.getMonthValue()-joinDate.getMonthValue();
	        System.out.print("   "+7*x/6+"   ");
	        System.out.print("   "+daysDifference+"   ");
	        System.out.print("   "+leaveTime+"   ");
	        if(joinDate.getDayOfMonth() >= startDate.getDayOfMonth())
	        	x=x-1;
	        		
	        Integer y= 14 * (12 - joinDate.getMonthValue()) / 12;
	        System.out.print(x*y/(12-joinDate.getMonthValue()));
	        //吼吼
	        if (x*y/(12-joinDate.getMonthValue())>=daysDifference && daysDifference<leaveTime)
	        	return true;
	        else
	        	return false;

        }
        	
        else {
        	if(startDate.getMonth() == Month.JANUARY && "Annual".equals(leaveType))
        		leaveTime=5;
        	return leaveTime >= daysDifference;
        }
    }
    
    
    
    public boolean checkAvailabilityForTwoYears(Integer userId, LocalDate startDate, Integer Year,Integer Year2, String leaveType, Integer daysDifference) {

    	//这里比较两年数据，一定是老员工，并且只可能是annual，倒是没必要看status了
        User usertest=userRepository.getById(userId);
        LeaveRemainDTO newLeave = leaveRemainService.getLeaveRemainByUserIDAndYear(userId, Year);
        LeaveRemainDTO newLeave2 = leaveRemainService.getLeaveRemainByUserIDAndYear(userId, Year2);
        //System.out.print("   "+usertest.getJoinDate().getYear()+"   "+startDate.getYear()+"   "+startDate.getMonth()+"   "+"Annual".equals(leaveType));

        Integer leaveTime = newLeave.getAnnualRemain();
        
        //最大五天
        if(leaveTime>5)
        	leaveTime=5;
        
        Integer leaveTime2 = newLeave2.getAnnualRemain();
        //两年数据都拿出来做比较
        
        if((leaveTime+leaveTime2)>=daysDifference)
        {
        	//如果两年数据都不能满足条件，就炸裂
        	return true;
        }
        
        else return false;
    }
    
    
    
    public LeaveRemainDTO countRemain(Integer userId, String userName, Integer year, Integer applyTime, Integer annualRemain , Integer sickRemain , Integer compassionLeave , 
    		Integer companyLeave , Integer birthdayRemain ,Integer timeOffLeave , Integer unpaidLeave , Integer hospitalRemain , String leaveType) {
        LeaveRemainDTO newRemain = new LeaveRemainDTO(userId, userName, year, null, null, null, null, null, null, null, null);
        switch (leaveType) {
            case "Annual":
                newRemain.setCompassionLeave(compassionLeave);
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain - applyTime);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave);
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Sick":
            	newRemain.setCompassionLeave(compassionLeave);
                newRemain.setSickRemain(sickRemain - applyTime);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave);
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Compassion":
            	newRemain.setCompassionLeave(compassionLeave + applyTime);
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave);
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Birthday":
            	newRemain.setCompassionLeave(compassionLeave );
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain - applyTime);
                newRemain.setCompanyLeave(companyLeave);
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Company":
            	newRemain.setCompassionLeave(compassionLeave );
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave + applyTime);
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "TimeOff":
            	System.out.print(applyTime);
            	newRemain.setCompassionLeave(compassionLeave );
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave );
                newRemain.setTimeOffLeave(timeOffLeave + applyTime);
                newRemain.setUnpaidLeave(unpaidLeave);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Unpaid":
            	newRemain.setCompassionLeave(compassionLeave );
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave );
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave + applyTime);
                newRemain.setHospitalRemain(hospitalRemain);
                break;
            case "Hospital":
            	newRemain.setCompassionLeave(compassionLeave );
                newRemain.setSickRemain(sickRemain);
                newRemain.setAnnualRemain(annualRemain);
                newRemain.setBirthdayRemain(birthdayRemain);
                newRemain.setCompanyLeave(companyLeave );
                newRemain.setTimeOffLeave(timeOffLeave);
                newRemain.setUnpaidLeave(unpaidLeave );
                newRemain.setHospitalRemain(hospitalRemain - applyTime);
                break;
            default:
                System.out.print(false);
        }
        return newRemain;
    }

    // 定义法定节假日列表
    private static final Map<String,MonthDay> publicHolidays = new HashMap<>();

    static {
        publicHolidays.put("New Year's Day",MonthDay.of(1, 1));
        publicHolidays.put("Good Friday",MonthDay.of(3,29));
        publicHolidays.put("Labour Day",MonthDay.of(5,1));
        publicHolidays.put("National Day",MonthDay.of(8,9));
        publicHolidays.put("Christmas Day",MonthDay.of(12, 25));

    }

    // 判断是否为工作日
    public static boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY
                && !isHoliday(date);
    }
    
    
    public static boolean isHoliday(LocalDate date) {
        MonthDay monthDay = MonthDay.of(date.getMonth(), date.getDayOfMonth());
        return publicHolidays.containsValue(monthDay);
    }

    //
    // 计算两个日期之间的工作日天数
    public static Integer countWorkingDays(LocalDate startDate, LocalDate endDate) {
        Integer workingDays = 0;
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (isWorkingDay(currentDate)) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return workingDays;
    }



}
