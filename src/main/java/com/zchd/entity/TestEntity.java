package com.zchd.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 鲍安和
 * @date 2024/2/3 9:17
 */
@Data
public class TestEntity {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
