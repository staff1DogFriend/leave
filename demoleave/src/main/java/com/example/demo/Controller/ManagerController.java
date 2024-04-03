package com.example.demo.Controller;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Entity.ManagerLeaveApplicationDTO;
import com.example.demo.Entity.User;
import com.example.demo.Service.AdminService;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.LeaveRemainService;
import com.example.demo.Service.ManagerLeaveApplicationService;
import com.example.demo.Service.ManagerService;
import com.example.demo.Service.UserService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;

@Controller
public class ManagerController {
    @Autowired
    ManagerService managerService;

    @Autowired
    private ManagerLeaveApplicationService managerleaveApplicationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private LeaveRemainService leaveRemainService;
    
    @Autowired
    private AdminService adminService;
    
   

    @GetMapping("/Manager")
    public String ManagerControl(){
        return "managerWelcome";
    }

    @GetMapping("/Manager/Leaves")
    public String showLeaveRemain(HttpSession session, Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size,
                                   @RequestParam(required = false) String username,
                                   @RequestParam(required = false) Integer userId,
                                   @RequestParam(required = false) Integer year,
                                   @RequestParam(required = false) Integer month,
                                   @RequestParam(required = false) String from) {
        // 从URL中获取搜索条件
        // 注意：这里的参数名需要与搜索方法中的参数名一致
        String usernameParam = username;
        Integer userIdParam = userId;
        Integer yearParam = year;
        Integer monthParam = month;

        // 处理分页请求
        User user1 = (User) session.getAttribute("user");
        Integer id = user1.getId();
        Page<ManagerLeaveApplicationDTO> leavesPage;
        if ("Admin".equals(user1.getJobTitle()))
            leavesPage = managerleaveApplicationService.getAppliedLeaveApplicationsForAdmin(id, PageRequest.of(page, size));
        else
            leavesPage = managerleaveApplicationService.getAppliedLeaveApplicationsByApproverAndManager(id, PageRequest.of(page, size));

        model.addAttribute("leavesPage", leavesPage);

        // 将搜索条件添加到模型中，以便在页面上显示
        model.addAttribute("username", usernameParam);
        model.addAttribute("userId", userIdParam);
        model.addAttribute("year", yearParam);
        model.addAttribute("month", monthParam);
        model.addAttribute("from", from);

        return "manager";
    }
    
   
    @GetMapping("/Manager/Rejected")
    public String showRejectedLeave(HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String from){
    	User user2 = (User) session.getAttribute("user");
    	Integer id=user2.getId();
    	
    	Page<ManagerLeaveApplicationDTO> leavesPage;
    	if ("Admin".equals(user2.getJobTitle())) {
    	    leavesPage = managerleaveApplicationService.getRejectedLeaveApplicationsForAdmin(id, PageRequest.of(page, size));
    	} else {
    	    leavesPage = managerleaveApplicationService.getRejectedLeaveApplicationsByApproverAndManager(id, PageRequest.of(page, size));
    	}
    	
    	Map<Integer, String> supervisorNamesMap = new HashMap<>();
        leavesPage.getContent().forEach(leave -> {
            String supervisorName = userService.getUserById(leave.getApproverID()).getName();
            supervisorNamesMap.put(leave.getApplicationId(), supervisorName);
        });
    	model.addAttribute("leavesPage",leavesPage);
    	model.addAttribute("supervisorNamesMap", supervisorNamesMap);
    	
        String usernameParam = username;
        Integer userIdParam = userId;
        Integer yearParam = year;
        Integer monthParam = month;
        // 将搜索条件添加到模型中，以便在页面上显示
        model.addAttribute("username", usernameParam);
        model.addAttribute("userId", userIdParam);
        model.addAttribute("year", yearParam);
        model.addAttribute("month", monthParam);
        model.addAttribute("from", from);
        return "managerRejected";
    }
    
    @GetMapping("/Manager/Approved")
    public String showApprovedLeave(HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String from){
    	User user2 = (User) session.getAttribute("user");
    	Integer id=user2.getId();
    	
    	Page<ManagerLeaveApplicationDTO> leavesPage;
    	if ("Admin".equals(user2.getJobTitle())) {
    	    leavesPage = managerleaveApplicationService.getApprovedLeaveApplicationsForAdmin(id, PageRequest.of(page, size));
    	} else {
    	    leavesPage = managerleaveApplicationService.getApprovedLeaveApplicationsByApproverAndManager(id, PageRequest.of(page, size));
    	}
    	
    	Map<Integer, String> supervisorNamesMap = new HashMap<>();
        leavesPage.getContent().forEach(leave -> {
            String supervisorName = userService.getUserById(leave.getApproverID()).getName();
            supervisorNamesMap.put(leave.getApplicationId(), supervisorName);
        });
    	model.addAttribute("leavesPage",leavesPage);
    	model.addAttribute("supervisorNamesMap", supervisorNamesMap);
    	
        String usernameParam = username;
        Integer userIdParam = userId;
        Integer yearParam = year;
        Integer monthParam = month;
        // 将搜索条件添加到模型中，以便在页面上显示
        model.addAttribute("username", usernameParam);
        model.addAttribute("userId", userIdParam);
        model.addAttribute("year", yearParam);
        model.addAttribute("month", monthParam);
        model.addAttribute("from", from);
        return "managerApproved";
    }
    
    @GetMapping("/operateLeave")
    public String EditForm(@RequestParam("applicationId") Integer Id,
    		//这个应该是Leaveapplication的id
                           Model model) {
        ManagerLeaveApplicationDTO leaveApplicationDTO = managerleaveApplicationService.getLeaveApplicationByApplicationId(Id);
        if(leaveApplicationDTO==null){
            return "redirect:/Manager/Leaves";
        }
        LocalDateTime databaseTime = leaveApplicationDTO.getSubmitDate();

        // 减去16小时，换了两次emm
        LocalDateTime singaporeTime = databaseTime.atZone(ZoneId.of("UTC"))
                                             .withZoneSameInstant(ZoneId.of("Asia/Singapore"))
                                             .minusHours(16)
                                             .toLocalDateTime();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yy, h:mm a");
        String formattedDateTime = singaporeTime.format(format);
        // 将新加坡时间添加到模型中
        model.addAttribute("MySubmit", formattedDateTime);

        model.addAttribute("leaveApplicationDTO", leaveApplicationDTO);
        return "manageroperateLeave";
    }

    @PostMapping("/operateLeave")
    public ModelAndView updateLeaveRemain(@ModelAttribute("leaveApplicationDTO") ManagerLeaveApplicationDTO leaveApplicationDTO,Model model) {
    	leaveApplicationDTO.setApproveDate();
        managerleaveApplicationService.updateLeaveApplication(leaveApplicationDTO);
        //System.out.println("Current ZoneId: " + leaveApplicationDTO.getApproveDate());
        
        //这里需要执行一个判断approved或rejected，并把通过的数据更新到leave_remain的操作
        managerleaveApplicationService.updateLeaveRemain(leaveApplicationDTO);

        emailService.sendOperateLeaveApplicationEmail(leaveApplicationDTO.getUserID(),leaveApplicationDTO.getLeaveType(),leaveApplicationDTO.getStatus());
        
        return new ModelAndView("redirect:/Manager/Leaves");
    }

    // this one seems to be useless
    @PostMapping("/Manager/operateLeave")
    //这个原来是userid需要调整一下，命名要对齐
    public String operateLeave(@ModelAttribute ManagerLeaveApplicationDTO leaveApplicationDTO) {
        try {
            managerService.operateLeave(leaveApplicationDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "redirect:/Manager";
    }

    
    @GetMapping("/Manager/searchLeaves")
    public String searchLeaves(Model model, HttpSession session,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) Integer userId,
                                @RequestParam(required = false) Integer year,
                                @RequestParam(required = false) Integer month,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "8") int size,
                                @RequestParam(required = false) String from) {

        if (username == null && userId == null) {
            return "redirect:/Manager"; // 返回到管理界面
        }

        User user3 = (User) session.getAttribute("user");
        Integer id = user3.getId();
        Page<ManagerLeaveApplicationDTO> leavesPage = null;
        if ("Applied".equals(from))
            leavesPage = managerleaveApplicationService.searchLeavesByStatusAndUser(id, user3.getJobTitle(), "Applied", username, userId, year, month, PageRequest.of(page, size));
        else if ("Approved".equals(from))
            leavesPage = managerleaveApplicationService.searchLeavesByStatusAndUser(id, user3.getJobTitle(), "Approved", username, userId, year, month, PageRequest.of(page, size));
        else
            leavesPage = managerleaveApplicationService.searchLeavesByStatusAndUser(id, user3.getJobTitle(), "Rejected", username, userId, year, month, PageRequest.of(page, size));

        model.addAttribute("leavesPage", leavesPage);
        model.addAttribute("username", username); // 将用户名添加到模型中
        model.addAttribute("userId", userId); // 将用户ID添加到模型中
        model.addAttribute("year", year); // 将年份添加到模型中
        model.addAttribute("month", month); // 将月份添加到模型中
        model.addAttribute("from", from); // 将来源添加到模型中
        model.addAttribute("page", page); // Add page to retain pagination state
        model.addAttribute("size", size); // Add size to retain pagination state

        if ("Applied".equals(from))
            return "manager";
        else if ("Approved".equals(from)) {
            Map<Integer, String> supervisorNamesMap = new HashMap<>();
            leavesPage.getContent().forEach(leave -> {
                String supervisorName = userService.getUserById(leave.getApproverID()).getName();
                supervisorNamesMap.put(leave.getApplicationId(), supervisorName);
            });
            model.addAttribute("supervisorNamesMap", supervisorNamesMap);
            return "managerApproved";
        } else {
            Map<Integer, String> supervisorNamesMap = new HashMap<>();
            leavesPage.getContent().forEach(leave -> {
                String supervisorName = userService.getUserById(leave.getApproverID()).getName();
                supervisorNamesMap.put(leave.getApplicationId(), supervisorName);
            });
            model.addAttribute("supervisorNamesMap", supervisorNamesMap);
            return "managerRejected";
        }
    }

    
    
    @GetMapping("/Manager/LeaveBalance")
    public String showLeaveRemain(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size){
        Page<LeaveRemainDTO> leavesPage=leaveRemainService.GetAllLeaveRemains(PageRequest.of(page,size));
        model.addAttribute("leavesPage",leavesPage);
        return "managerLeaveManagement";
    }

    @GetMapping("/Manager/editLeaveBalance")
    public String editLeaveForm(@RequestParam("userID") Integer userID,
                                @RequestParam("year") Integer year,
                                Model model) {
        LeaveRemainDTO leaveRemainDTO = leaveRemainService.getLeaveRemainByUserIDAndYear(userID, year);
        if(leaveRemainDTO==null){
            return "redirect:/Manager/LeaveBalance";
        }
        model.addAttribute("leaveRemainDTO", leaveRemainDTO);
        return "managerEditLeave";
    }

    @PostMapping("/Manager/editLeaveBalance")
    public ModelAndView updateLeaveRemain(@ModelAttribute("leaveRemainDTO") LeaveRemainDTO leaveRemainDTO) {
        leaveRemainService.updateLeaveRemain(leaveRemainDTO);
        return new ModelAndView("redirect:/Manager/LeaveBalance");
    }

    @GetMapping("/Manager/searchLeaveBalance")
    public String searchLeaves(Model model,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) Integer userId,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "8") int size) {
        Page<LeaveRemainDTO> leavesPage = leaveRemainService.searchLeave(username, userId, year, PageRequest.of(page, size));
        model.addAttribute("leavesPage", leavesPage);
        model.addAttribute("username", username); // 传递搜索参数回页面
        model.addAttribute("userId", userId); // 传递搜索参数回页面
        model.addAttribute("jobTitle", year); // 传递搜索参数回页面
        return "managerLeaveManagement";
    }

    @GetMapping("/Manager/deleteLeaveBalance")
    public String deleteLeave(@RequestParam("userID") Integer userID,
                              @RequestParam("year") Integer year) {
        leaveRemainService.deleteLeave(userID, year);
        return "redirect:/Manager/LeaveBalance";
    }
    


    
    @PostMapping("/Manager/addLeaveBalance")
    public String addLeave(@RequestParam Integer userId,
                           @RequestParam Integer year,
                           @RequestParam Integer AnnualRemain,
                           @RequestParam Integer SickRemain,
                           @RequestParam Integer CompassionLeave,
                           @RequestParam Integer CompanyLeave,
                           @RequestParam Integer BirthdayRemain,
                           @RequestParam Integer TimeOffLeave,
                           @RequestParam Integer UnpaidLeave,
                           @RequestParam Integer HospitalRemain
    		) {
        adminService.addLeave(userId, year, AnnualRemain, SickRemain, CompassionLeave,
        		CompanyLeave,BirthdayRemain,TimeOffLeave,UnpaidLeave,HospitalRemain);
        return "redirect:/Manager/LeaveBalance";
    }
    
    @PostMapping("/Manager/autoGenerateLeaveRemainForAllUsers")
    public String autoGenerateLeaveRemainForAllUsers(@RequestParam Integer year) {
    	List<User> users = userService.getAllUsers(); 
        for (User user : users) {
        	adminService.AutoAddLeaveRemainForOneYear(user.getId(), year);
        }
        return "redirect:/Manager/LeaveBalance"; // 重定向到显示请假记录的页面
    }
}