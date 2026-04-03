package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_quarter")
public class QuarterStat {
    @TableId(type = IdType.NONE)
    private Long id;
    private Long matchId;
    private Integer quarterNum;
    private String team1Name;
    private String team2Name;
    private Integer team1Score;
    private Integer team2Score;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
