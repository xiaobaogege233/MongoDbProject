package com.zchd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class EmployeeDTO {


    private Integer id;

    private String name;

    private int age;

    private String salaryStr;

    private String birthdayStr;
}
