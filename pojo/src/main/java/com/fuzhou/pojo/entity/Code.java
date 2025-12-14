package com.fuzhou.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    private Integer id;
    private String code;
    private String userEmail;
    private Boolean used;
    private LocalDateTime expireTime;
}
