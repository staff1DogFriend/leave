package com.example.demo.Entity;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveApplicationDTO {
    private Integer id;
    private String username;
    private String leaveType;
    private String reason;
    private String startDate;
    private String endDate;
    private String status;
    private String submitDate;
    private Integer approverID;
    private String approverName;
    private String approveDate;
    private String comment;
}
