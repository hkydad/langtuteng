package com.football.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.attendance.entity.Team;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {
}
