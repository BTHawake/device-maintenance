package com.awake.repair.dto;

import java.util.List;
//分页结构体
public record PageResult<T> (
    List<T> records,
    long total,
    long pages,
    long current,
    long size
){}
