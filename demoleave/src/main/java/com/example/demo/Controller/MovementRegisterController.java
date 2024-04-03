package com.example.demo.Controller;

import com.example.demo.Entity.LeaveApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.Service.LeaveApplicationService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;

@Controller
public class MovementRegisterController {
    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @GetMapping("/MovementRegister")
    public String movementRegister(@RequestParam(required = false) Integer year,
                                   @RequestParam(required = false) Integer month,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "8") Integer size,
                                   HttpSession session,
                                   Model model) {
        LocalDate now = LocalDate.now();
        if (year == null) {
            year = now.getYear();
        }
        if (month == null) {
            month = now.getMonthValue();
        }

        // 存储搜索参数到会话
        session.setAttribute("searchYear", year);
        session.setAttribute("searchMonth", month);

        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveApplicationDTO> applications = leaveApplicationService.getLeaveApplicationByMonth(year, month, pageable);
        model.addAttribute("applications", applications);
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);

        return "movementRegister";
    }
}
