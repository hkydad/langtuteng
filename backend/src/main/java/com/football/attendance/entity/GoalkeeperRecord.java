package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match_goalkeeper")
public class GoalkeeperRecord {
    @TableId(type = IdType.NONE)
    private Long id;
    private Long quarterId;
    private Long playerId;
    private String playerName;
    private String teamName;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
