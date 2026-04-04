package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("player")
public class Player {
    @TableId(type = IdType.NONE)
    private Long id;
    private String name;
    private String phone;
    private Integer memberLevel;
    private String teamName;
    private Integer season;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
