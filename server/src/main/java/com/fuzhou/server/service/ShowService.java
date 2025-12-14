package com.fuzhou.server.service;

import com.fuzhou.common.result.PageResult;
import com.fuzhou.pojo.dto.HomePageDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface ShowService {
    PageResult show(HomePageDTO homePageDTO, HttpServletRequest request);
}
