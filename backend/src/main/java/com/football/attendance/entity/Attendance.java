package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.NONE)
    private Long id;
    private Long matchId;
    private Long playerId;
    private String status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
