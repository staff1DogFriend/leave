package com.example.demo.Controller;
import com.example.demo.Entity.LeaveFormDTO;
import com.example.demo.Service.LeaveRemainService;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/Admin")
@RestController
public class AdminApiController {
    @Autowired
    private LeaveRemainService leaveRemainService;
    @PostMapping("/checkLeaveExist")
    public boolean checkLeaveExist(@RequestBody LeaveFormDTO leaveFormDTO) {
        return leaveRemainService.checkIfRecordExists(leaveFormDTO.getUserId(), leaveFormDTO.getYear());
    }
}
