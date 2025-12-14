package com.fuzhou.server.mapper;

import com.fuzhou.pojo.vo.ShowVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShowMapper {
    List<ShowVO> selectByCity(String queryCity);
}
