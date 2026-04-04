package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("season")
public class Season {
    @TableId(type = IdType.NONE)
    private Long id;
    private Integer year;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
