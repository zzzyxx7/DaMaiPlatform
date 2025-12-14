package com.fuzhou.server.mapper;

import com.fuzhou.pojo.vo.ShowVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShowActorMapper {
    List<ShowVO.ActorVO> selectActorsByShowIds(@Param("showIds") List<Long> showIds);
}
