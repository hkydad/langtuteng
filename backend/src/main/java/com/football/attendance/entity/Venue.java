package com.football.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("venue")
public class Venue {
    @TableId(type = IdType.NONE)
    private Long id;
    private String name;
    private String address;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
