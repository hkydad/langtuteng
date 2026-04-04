package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team")
public class Team {
    @TableId(type = IdType.NONE)
    private Long id;
    private String name;
    private Integer season;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
