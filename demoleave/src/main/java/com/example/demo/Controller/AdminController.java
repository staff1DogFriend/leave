package com.example.demo.Controller;
import com.example.demo.Entity.LeaveRemainDTO;
import com.example.demo.Entity.User;
import com.example.demo.Service.LeaveRemainService;
import com.example.demo.Service.UserService;
import jakarta.servlet.http.HttpSession;

import java.sql.Date;
import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Service.AdminService;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private LeaveRemainService leaveRemainService;

    @GetMapping("/Admin")
    public String adminControl(){
        return "adminWelcome";
    }

    @GetMapping("/Admin/Leaves")
    public String showLeaveRemain(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size){
        Page<LeaveRemainDTO> leavesPage=leaveRemainService.GetAllLeaveRemains(PageRequest.of(page,size));
        model.addAttribute("leavesPage",leavesPage);
        return "adminLeaveManagement";
    }

    @GetMapping("/Admin/editLeave")
    public String editLeaveForm(@RequestParam("userID") Integer userID,
                                @RequestParam("year") Integer year,
                                Model model) {
        LeaveRemainDTO leaveRemainDTO = leaveRemainService.getLeaveRemainByUserIDAndYear(userID, year);
        if(leaveRemainDTO==null){
            return "redirect:/Admin/Leaves";
        }
        model.addAttribute("leaveRemainDTO", leaveRemainDTO);
        return "adminEditLeave";
    }

    @PostMapping("/Admin/editLeave")
    public ModelAndView updateLeaveRemain(@ModelAttribute("leaveRemainDTO") LeaveRemainDTO leaveRemainDTO) {
        leaveRemainService.updateLeaveRemain(leaveRemainDTO);
        return new ModelAndView("redirect:/Admin/Leaves");
    }

    @GetMapping("/Admin/searchLeaves")
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
        
        return "adminLeaveManagement";
    }

    @GetMapping("/Admin/deleteLeave")
    public String deleteLeave(@RequestParam("userID") Integer userID,
                              @RequestParam("year") Integer year) {
        leaveRemainService.deleteLeave(userID, year);
        return "redirect:/Admin/Leaves";
    }
    


    
    @PostMapping("/Admin/addLeave")
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
        return "redirect:/Admin/Leaves";
    }

    @GetMapping("/Admin/searchUsers")
    public String searchUsers(Model model,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) Integer userId,
                              @RequestParam(required = false) String jobTitle,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "8") int size) {
        Page<User> usersPage = userService.searchUsers(username, userId, jobTitle, PageRequest.of(page, size));
        model.addAttribute("usersPage", usersPage);
     // 创建一个Map来存储上级主管姓名，以User对象的ID为key

        Map<Integer, String> supervisorNamesMap = new HashMap<>();
        usersPage.getContent().forEach(user -> {
            // 获取上级主管姓名
            String supervisorName = userService.getUserById(user.getSupervisor()).getName();
            supervisorNamesMap.put(user.getId(), supervisorName);
        });

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("supervisorNamesMap", supervisorNamesMap); // 将Map添加到模型中
        

        List<User> managersAndAdmins = userService.getNotEmployeeUsers(); // 获取所有不是员工的管理员和经理的用户列表

        model.addAttribute("managersAndAdmins", managersAndAdmins); // 将管理员和经理的用户列表添加到模型中
        
        

        model.addAttribute("username", username); // 传递搜索参数回页面
        model.addAttribute("userId", userId); // 传递搜索参数回页面
        model.addAttribute("jobTitle", jobTitle); // 传递搜索参数回页面
        
        return "adminUserManagement";
    }

    @GetMapping("/Admin/deleteUser")
    public String deleteUser(@RequestParam("id") Integer id) {
        userService.deleteUser(id);
        return "redirect:/Admin/Users";
    }

    
    
    @PostMapping("/Admin/addUser")
    public String addUser(@RequestParam String name,
                          @RequestParam String password,
                          @RequestParam String jobTitle,
                          @RequestParam Integer supervisor,
                          @RequestParam Integer age,
                          @RequestParam Integer yearsOfWork,
                          @RequestParam String emailAddress,
                          @RequestParam Date joinDate,
                          @RequestParam Date birthday) {
        User newUser = new User();
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setJobTitle(jobTitle);
        newUser.setSupervisor(supervisor);
        newUser.setAge(age);
        newUser.setYearsOfWork(yearsOfWork);
        newUser.setEmailAddress(emailAddress);
        newUser.setJoinDate(joinDate);
        newUser.setDateOfBirth(birthday);
        
        userService.addUser(newUser);

        Integer userId = newUser.getId();
        Integer year = Year.now().getValue();

        adminService.AutoAddLeaveRemain(userId, year);

        return "redirect:/Admin/Users";
    }


    @GetMapping("/Admin/editUser")
    public String editUserForm(@RequestParam("id") Integer id, Model model) {
        User user = userService.getUserById(id);
        List<User> managersAndAdmins = userService.getNotEmployeeUsers(); // 获取所有不是员工的管理员和经理的用户列表

        model.addAttribute("user", user);
        model.addAttribute("managersAndAdmins", managersAndAdmins); // 将管理员和经理的用户列表添加到模型中
        return "adminEditUser";
    }

    @PostMapping("/Admin/editUser")
    public String processEditUserForm(@ModelAttribute("user") User editedUser, @RequestParam("supervisorId") Integer supervisorId) {
        // 处理用户选择的上级主管
    	//System.out.print("\n/n"+supervisorId+"\n/n");
    	
        User supervisor = userService.getUserById(supervisorId);
        editedUser.setSupervisor(supervisor.getId()); // 设置用户的上级主管为选择的主管对象

        // 更新用户信息
        userService.updateUser(editedUser);

        return "redirect:/Admin/Users"; // 重定向到用户管理页面
    }


    
    @GetMapping("/Admin/Users")
    public String showUsers(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size){
        Page<User> usersPage=userService.getAllUsers(PageRequest.of(page,size));
     // 创建一个Map来存储上级主管姓名，以User对象的ID为key
        Map<Integer, String> supervisorNamesMap = new HashMap<>();
        usersPage.getContent().forEach(user -> {
            // 获取上级主管姓名
            String supervisorName = userService.getUserById(user.getSupervisor()).getName();
            supervisorNamesMap.put(user.getId(), supervisorName);
        });

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("supervisorNamesMap", supervisorNamesMap); // 将Map添加到模型中
        

        List<User> managersAndAdmins = userService.getNotEmployeeUsers(); // 获取所有不是员工的管理员和经理的用户列表

        model.addAttribute("managersAndAdmins", managersAndAdmins); // 将管理员和经理的用户列表添加到模型中



        return "adminUserManagement";
    }
    
    
    
    @PostMapping("/Admin/autoGenerateLeaveRemainForAllUsers")
    public String autoGenerateLeaveRemainForAllUsers(@RequestParam Integer year) {
    	List<User> users = userService.getAllUsers(); 
        for (User user : users) {
        	adminService.AutoAddLeaveRemainForOneYear(user.getId(), year);
        }
        return "redirect:/Admin/Leaves"; // 重定向到显示请假记录的页面
    }
}

