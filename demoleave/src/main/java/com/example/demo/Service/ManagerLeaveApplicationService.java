package com.example.demo.Service;

import com.example.demo.Entity.LeaveRemainId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.Entity.ManagerLeaveApplication;
import com.example.demo.Entity.ManagerLeaveApplicationDTO;
import com.example.demo.Entity.LeaveRemain;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Repository.ManagerLeaveApplicationRepository;
import com.example.demo.Repository.LeaveRemainRepository;
import com.example.demo.Repository.ManagerRepository;
import com.example.demo.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.Entity.User;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerLeaveApplicationService {
    @Autowired
    private ManagerLeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveRemainRepository leaveRemainRepository;

    @Autowired
    private ManagerRepository managerRepository;
    
    @Autowired
    private UserService userService;
    //for manager
    

    public Page<ManagerLeaveApplicationDTO> getAppliedLeaveApplicationsByApproverAndManager(Integer managerId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByApproverIDAndStatus(managerId,"Applied", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }

    public Page<ManagerLeaveApplicationDTO> getNotAppliedLeaveApplicationsByApproverAndManager(Integer managerId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByApproverIDAndStatusNot(managerId,"Applied", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    public Page<ManagerLeaveApplicationDTO> getApprovedLeaveApplicationsByApproverAndManager(Integer managerId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByApproverIDAndStatus(managerId,"Approved", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    public Page<ManagerLeaveApplicationDTO> getRejectedLeaveApplicationsByApproverAndManager(Integer managerId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByApproverIDAndStatus(managerId,"Rejected", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    //for admin
    
    public Page<ManagerLeaveApplicationDTO> getAppliedLeaveApplicationsForAdmin(Integer adminId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByStatus("Applied", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    public Page<ManagerLeaveApplicationDTO> getNotAppliedLeaveApplicationsForAdmin(Integer adminId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByStatusNot("Applied", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    public Page<ManagerLeaveApplicationDTO> getApprovedLeaveApplicationsForAdmin(Integer adminId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByStatus("Approved", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    public Page<ManagerLeaveApplicationDTO> getRejectedLeaveApplicationsForAdmin(Integer adminId, Pageable pageable) {

        Page<ManagerLeaveApplication> leaveApplicationPage = leaveApplicationRepository.findByStatus("Rejected", pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    //for all
    public ManagerLeaveApplicationDTO getLeaveApplicationByApplicationId(Integer id) {
    	ManagerLeaveApplication leaveApplication = leaveApplicationRepository.findByApplicationId(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave application not found with id: " + id));

        return convertToLeaveApplicationDTO(leaveApplication);
    }


    
    public void updateLeaveApplication(ManagerLeaveApplicationDTO leaveApplicationDTO) {
        Integer leaveApplicationId = leaveApplicationDTO.getApplicationId();
        ManagerLeaveApplication leaveApplication = leaveApplicationRepository.findByApplicationId(leaveApplicationId).orElse(null);
        if (leaveApplication != null) {
            leaveApplication.setComment(leaveApplicationDTO.getComment());
            leaveApplication.setStatus(leaveApplicationDTO.getStatus());
            // 添加当前时间，并将其设置为本地时区时间
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            leaveApplication.setApproveDate(now);
            leaveApplicationRepository.save(leaveApplication);
        }
    }

    public long countWorkingDays(Date startDate, Date endDate) {
        long workingDays = 0;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        while (!startCalendar.after(endCalendar)) {
            // 检查当前日期是否是工作日（这里假设周六和周日是非工作日）
            if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                workingDays++;
            }
            // 增加当前日期
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return workingDays;
    }
    
    
    public void updateLeaveRemain(ManagerLeaveApplicationDTO leaveApplicationDTO) {
        Integer leaveApplicationId = leaveApplicationDTO.getApplicationId();
        ManagerLeaveApplication leaveApplication = leaveApplicationRepository.findByApplicationId(leaveApplicationId).orElse(null);

        if (leaveApplication != null && "Rejected".equals(leaveApplicationDTO.getStatus())) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy.M.d");
            String startDateStr = inputFormat.format(leaveApplicationDTO.getStartDate());
            String endDateStr = inputFormat.format(leaveApplicationDTO.getEndDate());

            try {
                Date startDate = new java.sql.Date(inputFormat.parse(startDateStr).getTime());
                Date endDate = new java.sql.Date(inputFormat.parse(endDateStr).getTime());

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);

                while (startCalendar.get(Calendar.YEAR) <= endCalendar.get(Calendar.YEAR)) {
                	System.out.println("start: " + startCalendar.get(Calendar.YEAR));
                	System.out.println("end: " + endCalendar.get(Calendar.YEAR));
                    int currentYear = startCalendar.get(Calendar.YEAR);

                    LeaveRemainId leaveRemainId = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear);
                    LeaveRemain leaveRemain = leaveRemainRepository.findById(leaveRemainId).orElse(null);

                    long daysDifference = countWorkingDays(startDate,endDate);
                    System.out.println("daysDifference: " + daysDifference);
                    int theDifference = 0;

                    switch (leaveApplicationDTO.getLeaveType()) {
                        case "Sick":
                            theDifference = leaveRemain.getSickRemain() + (int) daysDifference;
                            System.out.println("theDifference: " + theDifference);
                            if (theDifference > 14) {
                                theDifference = 14; // 将剩余天数设置为定值
                            }
                            leaveRemain.setSickRemain(theDifference);
                            break;
                        case "Annual"://different
                            theDifference = leaveRemain.getAnnualRemain() + (int) daysDifference;
                            User usertemp=userService.getUserById(leaveApplicationDTO.getUserID());
                            if (endCalendar.get(Calendar.MONTH) == Calendar.JANUARY) {
                                
                                Integer MaxDate=0;
                                Integer temp=startDate.getYear()-usertemp.getJoinDate().getYear();
                                
                                if(temp==0)
                    	        //萌新没有一月annual、这个if不可能发生
                    	        {
                                	MaxDate= 14 * (12 - usertemp.getJoinDate().getMonth()) / 12;
                    	        }
                                else if((temp)>2)
                                	MaxDate=14+temp-2;
                                else
                                	MaxDate=14;
                                
                                if(MaxDate>21)
                                	MaxDate=21;
                                
                                if(leaveRemain.getAnnualRemain()<MaxDate)
                                {
                                	if (theDifference > MaxDate) {
                                
                                		//一定是5
	                                    Integer moredays = theDifference - MaxDate;
	                                    
	                                    LeaveRemainId leaveRemainId2 = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear-1);
	                                    LeaveRemain leaveRemain2 = leaveRemainRepository.findById(leaveRemainId2).orElse(null);
	                                    System.out.print(leaveRemain2.getAnnualRemain());
	                                    leaveRemain2.setAnnualRemain(moredays+leaveRemain2.getAnnualRemain());
	                                    leaveRemainRepository.save(leaveRemain2);
	                                    
	                                    
	                                    leaveRemain.setAnnualRemain(MaxDate);
	                                    leaveRemainRepository.save(leaveRemain);
                                	}
                                	else
                                	{
                                		leaveRemain.setAnnualRemain(theDifference);
                                        leaveRemainRepository.save(leaveRemain);
                                	}
                                }
                                else {
                                	LeaveRemainId leaveRemainId2 = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear-1);
                                    LeaveRemain leaveRemain2 = leaveRemainRepository.findById(leaveRemainId2).orElse(null);
                                    int temp_number=leaveRemain2.getAnnualRemain();
                                    leaveRemain2.setAnnualRemain((int)daysDifference+temp_number);
                                    leaveRemainRepository.save(leaveRemain2);
                                }
                                break;
                            }
                            
                            Integer MaxDate=0;
                            Integer temp=startDate.getYear()-usertemp.getJoinDate().getYear();
                            if(temp==1)
                	        //萌新还想annual、
                	        {
                            	MaxDate= 14 * (12 - usertemp.getJoinDate().getMonth()) / 12;
                	        }
                            else if((temp)>2)
                            	MaxDate=14+temp-2;
                            else if(temp==2)
                            	MaxDate=14;
                            
                            if(MaxDate>21)
                            	MaxDate=21;
                            
                            if (theDifference > MaxDate) {
                                theDifference = MaxDate; // 将剩余天数设置为0，避免负数
                            }
                            if (theDifference > MaxDate) {
                                theDifference = MaxDate; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setAnnualRemain(theDifference);
                            break;
                        case "Compassion":
                            theDifference = leaveRemain.getCompassionLeave() - (int) daysDifference;
                            if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setCompassionLeave(theDifference);
                            break;
                        case "Company":
                        	theDifference = leaveRemain.getCompanyLeave() - (int) daysDifference;
                        	if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setCompanyLeave(theDifference);
                            break;
                        case "Birthday":
                        	theDifference = leaveRemain.getBirthdayRemain() + (int) daysDifference;
                        	if (theDifference > 1) {
                                theDifference = 1; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setBirthdayRemain(theDifference);
                          	break;
                        case "TimeOff":
                        	theDifference = leaveRemain.getTimeOffLeave() - (int) daysDifference;
                        	if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setTimeOffLeave(theDifference);
                          	break;
                        case "Unpaid":
                        	theDifference = leaveRemain.getUnpaidLeave() - (int) daysDifference;
                        	if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setUnpaidLeave(theDifference);
                          	break;
                        case "Hospital":
                        	theDifference = leaveRemain.getHospitalRemain() + (int) daysDifference;
                        	if (theDifference > 46) {
                                theDifference = 46; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setHospitalRemain(theDifference);
                          	break;
                        // Add other leave types as needed

                        default:
                            // Handle unknown leave types
                            break;
                    }

                    leaveRemainRepository.save(leaveRemain);

                    // Move to the next year
                    startCalendar.add(Calendar.YEAR, 1);

                    if (startCalendar.get(Calendar.YEAR) > endCalendar.get(Calendar.YEAR)) {
                        // 如果已经超过结束日期，提前终止循环
                        break;
                    }
                }
            } catch (ParseException e) {
                // 处理ParseException异常，例如打印错误信息或采取其他适当的措施
                e.printStackTrace();
            }
        }
    }

    //use for cancel history
    public void updateLeaveRemainForCancel(ManagerLeaveApplicationDTO leaveApplicationDTO) {
        Integer leaveApplicationId = leaveApplicationDTO.getApplicationId();
        ManagerLeaveApplication leaveApplication = leaveApplicationRepository.findByApplicationId(leaveApplicationId).orElse(null);

        if (leaveApplication != null && "Applied".equals(leaveApplicationDTO.getStatus())) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy.M.d");
            String startDateStr = inputFormat.format(leaveApplicationDTO.getStartDate());
            String endDateStr = inputFormat.format(leaveApplicationDTO.getEndDate());

            try {
                Date startDate = new java.sql.Date(inputFormat.parse(startDateStr).getTime());
                Date endDate = new java.sql.Date(inputFormat.parse(endDateStr).getTime());

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);

                while (startCalendar.get(Calendar.YEAR) <= endCalendar.get(Calendar.YEAR)) {
                	System.out.println("start: " + startCalendar.get(Calendar.YEAR));
                	System.out.println("end: " + endCalendar.get(Calendar.YEAR));
                    int currentYear = startCalendar.get(Calendar.YEAR);

                    LeaveRemainId leaveRemainId = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear);
                    LeaveRemain leaveRemain = leaveRemainRepository.findById(leaveRemainId).orElse(null);

                    
                    long daysDifference = countWorkingDays(startDate,endDate);
                    System.out.println("daysDifference: " + daysDifference);
                    int theDifference = 0;

                    switch (leaveApplicationDTO.getLeaveType()) {
                        case "Sick":
                            theDifference = leaveRemain.getSickRemain() + (int) daysDifference;
                            System.out.println("theDifference: " + theDifference);
                            if (theDifference > 14) {
                                theDifference = 14; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setSickRemain(theDifference);
                            break;
                        case "Annual"://different
                            theDifference = leaveRemain.getAnnualRemain() + (int) daysDifference;
                            User usertemp=userService.getUserById(leaveApplicationDTO.getUserID());
                            if (endCalendar.get(Calendar.MONTH) == Calendar.JANUARY) {
                                
                                Integer MaxDate=0;
                                Integer temp=startDate.getYear()-usertemp.getJoinDate().getYear();
                                
                                if(temp==0)
                    	        //萌新没有一月annual、这个if不可能发生
                    	        {
                                	MaxDate= 14 * (12 - usertemp.getJoinDate().getMonth()) / 12;
                    	        }
                                else if((temp)>2)
                                	MaxDate=14+temp-2;
                                else
                                	MaxDate=14;
                                
                                if(MaxDate>21)
                                	MaxDate=21;
                                
                                if(leaveRemain.getAnnualRemain()<MaxDate)
                                {
                                	if (theDifference > MaxDate) {
                                
                                		//一定是5
	                                    Integer moredays = theDifference - MaxDate;
	                                    
	                                    LeaveRemainId leaveRemainId2 = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear-1);
	                                    LeaveRemain leaveRemain2 = leaveRemainRepository.findById(leaveRemainId2).orElse(null);
	                                    System.out.print(leaveRemain2.getAnnualRemain());
	                                    leaveRemain2.setAnnualRemain(moredays+leaveRemain2.getAnnualRemain());
	                                    leaveRemainRepository.save(leaveRemain2);
	                                    
	                                    
	                                    leaveRemain.setAnnualRemain(MaxDate);
	                                    leaveRemainRepository.save(leaveRemain);
                                	}
                                	else
                                	{
                                		leaveRemain.setAnnualRemain(theDifference);
                                        leaveRemainRepository.save(leaveRemain);
                                	}
                                }
                                else {
                                	LeaveRemainId leaveRemainId2 = new LeaveRemainId(leaveApplicationDTO.getUserID(), currentYear-1);
                                    LeaveRemain leaveRemain2 = leaveRemainRepository.findById(leaveRemainId2).orElse(null);
                                    int temp_number=leaveRemain2.getAnnualRemain();
                                    leaveRemain2.setAnnualRemain((int)daysDifference+temp_number);
                                    leaveRemainRepository.save(leaveRemain2);
                                }
                                break;
                            }
                            
                            Integer MaxDate=0;
                            Integer temp=startDate.getYear()-usertemp.getJoinDate().getYear();
                            if(temp==1)
                	        //萌新还想annual、
                	        {
                            	MaxDate= 14 * (12 - usertemp.getJoinDate().getMonth()) / 12;
                	        }
                            else if((temp)>2)
                            	MaxDate=14+temp-2;
                            else if(temp==2)
                            	MaxDate=14;
                            
                            if(MaxDate>21)
                            	MaxDate=21;
                            
                            if (theDifference > MaxDate) {
                                theDifference = MaxDate; // 将剩余天数设置为0，避免负数
                            }
                            if (theDifference > MaxDate) {
                                theDifference = MaxDate; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setAnnualRemain(theDifference);
                            break;
                        case "Compassion":
                            theDifference = leaveRemain.getCompassionLeave() - (int) daysDifference;
                            if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setCompassionLeave(theDifference);
                            
                            break;
                        case "Company":
                        	theDifference = leaveRemain.getCompanyLeave() - (int) daysDifference;
                            if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setCompanyLeave(theDifference);
                            
                            break;
                        case "Birthday":
                        	theDifference = leaveRemain.getBirthdayRemain() + (int) daysDifference;
                        	if (theDifference > 1) {
                                theDifference = 1; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setBirthdayRemain(theDifference);
                          	break;
                        case "TimeOff":
                        	theDifference = leaveRemain.getTimeOffLeave() - (int) daysDifference;
                            if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setTimeOffLeave(theDifference);
                            
                          	break;
                        case "Unpaid":
                        	theDifference = leaveRemain.getUnpaidLeave() - (int) daysDifference;
                            if (theDifference < 0) {
                                theDifference = 0; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setUnpaidLeave(theDifference);
                            
                          	break;
                        case "Hospital":
                        	theDifference = leaveRemain.getHospitalRemain() + (int) daysDifference; 
                        	if (theDifference > 46) {
                                theDifference = 46; // 将剩余天数设置为0，避免负数
                            }
                            leaveRemain.setHospitalRemain(theDifference);
                          	break;
                        // Add other leave types as needed

                        default:
                            // Handle unknown leave types
                            break;
                    }

                    leaveRemainRepository.save(leaveRemain);

                    // Move to the next year
                    startCalendar.add(Calendar.YEAR, 1);

                    if (startCalendar.get(Calendar.YEAR) > endCalendar.get(Calendar.YEAR)) {
                        // 如果已经超过结束日期，提前终止循环
                        break;
                    }
                }
            } catch (ParseException e) {
                // 处理ParseException异常，例如打印错误信息或采取其他适当的措施
                e.printStackTrace();
            }
        }
    }
    

    public ManagerLeaveApplicationDTO convertToLeaveApplicationDTO(ManagerLeaveApplication leaveApplication) {
    	ManagerLeaveApplicationDTO leaveApplicationDTO = new ManagerLeaveApplicationDTO(leaveApplication.getApplicationId(), leaveApplication.getApproverID());
        leaveApplicationDTO.setUserID(leaveApplication.getUser().getId());
        leaveApplicationDTO.setUserName(leaveApplication.getUser().getName());
        leaveApplicationDTO.setReason(leaveApplication.getReason());
        leaveApplicationDTO.setLeaveType(leaveApplication.getLeaveType());
        leaveApplicationDTO.setStartDate(leaveApplication.getStartDate());
        leaveApplicationDTO.setEndDate(leaveApplication.getEndDate());
        leaveApplicationDTO.setSubmitDate(leaveApplication.getSubmitDate());
        leaveApplicationDTO.setApproverID(leaveApplication.getApproverID());
        leaveApplicationDTO.setComment(leaveApplication.getComment());
        leaveApplicationDTO.setStatus(leaveApplication.getStatus());
        leaveApplicationDTO.setApproveDate(leaveApplication.getApproveDate());

        // 其他字段设置...
        //System.out.println("userid" + ": " + leaveApplicationDTO.getApplicationId());
        //System.out.println("userid" + ": " + leaveApplicationDTO.getUserID());
        //System.out.println("date: " + leaveApplicationDTO.getApproveDate());
        //System.out.println("status:  " + leaveApplicationDTO.getStatus());

        return leaveApplicationDTO;
    }

    
    
    public Page<ManagerLeaveApplicationDTO> searchLeave(String username, Integer userId, Pageable pageable) {
        Specification<ManagerLeaveApplication> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("user").get("name"), username));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<ManagerLeaveApplication> leaveApplicationPage = managerRepository.findAll(spec, pageable);
        return leaveApplicationPage.map(this::convertToLeaveApplicationDTO);
    }
    
    //really a bad way to search. should have been used the html to control this
    public Page<ManagerLeaveApplicationDTO> searchLeavesByStatusAndUser(Integer managerId,String jobtitle,String status, String username,Integer userId, Integer year, Integer month, Pageable pageable
    		) {
    	if ("Admin".equals(jobtitle)) {
            if (username != null && userId != null && year != null && month != null) {
                return leaveApplicationRepository.findByStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYearAndStartDateMonth(status, username, userId, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else if (userId != null && username == null && year != null && month != null) {
                return leaveApplicationRepository.findByStatusAndUser_IdAndStartDateYearAndStartDateMonth(status, userId, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } 
            else if (userId == null &&username != null && year != null && month != null) {
                return leaveApplicationRepository.findByStatusAndUser_NameContainingIgnoreCaseAndStartDateYearAndStartDateMonth(status, username, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }
            else if (username != null && year != null ) {
                return leaveApplicationRepository.findByStatusAndUser_NameContainingIgnoreCaseAndStartDateYear(status, username, year, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }  else if (username != null && userId != null) {
                return leaveApplicationRepository.findByStatusAndUser_NameContainingIgnoreCaseAndUser_Id(status, username, userId, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else if (year != null && month != null) {
                return leaveApplicationRepository.findByStatusAndYearAndMonth(status, year,month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            
    		}
            else if (year != null && userId != null) {
                return leaveApplicationRepository.findByStatusAndUser_IdAndStartDateYear( status, userId,year , pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } 
            else if (username != null) {
                return leaveApplicationRepository.findByStatusAndUser_NameContainingIgnoreCase(status, username, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else if (userId != null) {
                return leaveApplicationRepository.findByStatusAndUser_Id(status, userId, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else {
                return Page.empty();
            }
        } else {
            if (username != null && userId != null && year != null && month != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndUser_IdAndStartDateYearAndStartDateMonth(managerId, status, username, userId, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }  else if (userId != null && username==null && year != null && month != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_IdAndStartDateYearAndStartDateMonth(managerId, status, userId, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } 
            else if (userId==null && username != null && year != null && month != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndStartDateYearAndStartDateMonth(managerId,status, username, year, month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }else if (username != null && year != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndStartDateYear(managerId, status, username, year, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }
            else if (year != null && month != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndStartDateYearAndStartDateMonth(managerId,status, year,month, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else if (username != null && userId != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_NameContainingIgnoreCaseAndUser_Id(managerId, status, username, userId, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }
            else if (year != null && userId != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_IdAndStartDateYear(managerId, status, userId,year , pageable)
                        .map(this::convertToLeaveApplicationDTO);
            }
            else if (username != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_NameContainingIgnoreCase(managerId, status, username, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else if (userId != null) {
                return leaveApplicationRepository.findByApproverIDAndStatusAndUser_Id(managerId, status, userId, pageable)
                        .map(this::convertToLeaveApplicationDTO);
            } else {
                return Page.empty();
            }
        }
    }

}
