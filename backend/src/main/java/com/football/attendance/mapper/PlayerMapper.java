package com.football.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.attendance.entity.Player;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlayerMapper extends BaseMapper<Player> {
}
