package com.zchd.entity;

import lombok.Data;

@Data
public class EmployeeVO<T> {


    private Integer id;

    private String name;

    private T t;
}
