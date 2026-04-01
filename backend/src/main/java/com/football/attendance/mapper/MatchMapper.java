package com.football.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.attendance.entity.Match;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MatchMapper extends BaseMapper<Match> {
}
