package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("football_match")
public class Match {
    @TableId(type = IdType.NONE)
    private Long id;
    private LocalDate matchDate;
    private String location;
    private Integer season;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
