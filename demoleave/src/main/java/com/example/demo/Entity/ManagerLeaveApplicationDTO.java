package com.example.demo.Entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerLeaveApplicationDTO {
    private Integer ApplicationId;
    private Integer UserID;
    private String UserName;
    private String LeaveType;
    private String Reason;
    private Date StartDate;
    private Date EndDate;
    private String Status;
    private String Comment;
    private LocalDateTime SubmitDate;
    private LocalDateTime ApproveDate;
    private Integer ApproverID;

    
    private String username;
    
    public ManagerLeaveApplicationDTO(Integer applicationId, Integer approverId) {
        ApplicationId = applicationId;
        ApproverID = approverId;
    }
    
    public void setApproveDate() {
    	ZoneId singaporeZone = ZoneId.of("Asia/Singapore");
        // 获取当前时间，并使用新加坡时区
        this.ApproveDate = LocalDateTime.now(singaporeZone);
        System.out.println("Current ZoneId: " + ApproveDate);
    }
    
    

    //话说这样就没有编写status和comment的功能了？还是说只要用setter就可以完成？
    
    
    //@Override
    /*不知道有啥用先注释了
    public String toString() {
        return "LeaveRemainDTO{" +
                "userID=" + userID +
                "username='" + username + '\'' +
                ", year=" + year +
                ", AnnualRemain=" + AnnualRemain +
                ", SickRemain=" + SickRemain +
                ", Comment=" + Comment +
                '}';
    }
    */
}
