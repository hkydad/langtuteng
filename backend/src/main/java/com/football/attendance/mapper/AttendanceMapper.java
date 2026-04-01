package com.football.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.attendance.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}
