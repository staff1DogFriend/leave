package com.example.demo.Controller;
import com.example.demo.Entity.LeaveApplicationDTO;
import com.example.demo.Entity.ManagerLeaveApplication;
import com.example.demo.Entity.ManagerLeaveApplicationDTO;
import com.example.demo.Entity.User;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.LeaveApplicationService;
import com.example.demo.Service.ManagerLeaveApplicationService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class ViewHistoryController {
    @Autowired
    private LeaveApplicationService leaveApplicationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ManagerLeaveApplicationService managerleaveApplicationService;
    

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/{jobTitle}/{userID}/History")
    public String viewHistory(@PathVariable String jobTitle,
                              @PathVariable String userID,
                              Model model,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<LeaveApplicationDTO> leaveApplications = leaveApplicationService.getLeaveApplicationForUser(user.getId());
        
        // 对每个 leaveApplication 的日期时间字段进行处理
        leaveApplications.forEach(leaveApplicationDTO -> {
            // 处理 SubmitDate
            String utcSubmitTime = leaveApplicationDTO.getSubmitDate();
            String submitDateTimeString = parseAndConvertToSingaporeTimeString(utcSubmitTime);
            leaveApplicationDTO.setSubmitDate(submitDateTimeString);
        });

        model.addAttribute("leaveApplications", leaveApplications);
        return "managerhistory";
    }
    
    // 解析  时间并转换为新加坡时间字符串
    private String parseAndConvertToSingaporeTimeString(String utcTime) {
        if (utcTime == null || utcTime.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime databaseTime = LocalDateTime.parse(utcTime, formatter);


        LocalDateTime singaporeTime = databaseTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Singapore"))
                .minusHours(16)
                .toLocalDateTime();

        return singaporeTime.format(formatter);
    }

    @PostMapping("/{jobTitle}/{userID}/History/cancel")
    public String cancelApplication(@PathVariable String jobTitle,
    		@PathVariable String userID, 
    		HttpSession session, 
    		Model model, 
    		@RequestParam Integer applicationId,
    		@RequestParam String leaveType,
    		@RequestParam String status,
    		@RequestParam String startDate,
    		@RequestParam String endDate
    		) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        System.out.print(status);
        if ("Approved".equals(status))
        	{System.out.print("Yes Approved");
        	emailService.sendCancelEmail(user.getId(),leaveType,startDate,endDate);
        	}
        
        else
        	{System.out.print("Yes Applied");
        	
        	ManagerLeaveApplicationDTO leaveApplicationDTO = managerleaveApplicationService.getLeaveApplicationByApplicationId(applicationId);
        	managerleaveApplicationService.updateLeaveRemainForCancel(leaveApplicationDTO);
        	leaveApplicationService.deleteLeaveApplication(applicationId);}
        return "redirect:/{jobTitle}/{userID}/History";
    }

}
