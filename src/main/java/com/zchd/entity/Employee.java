package com.zchd.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private Integer id;
    @Field("username")
    private String name;
    @Field
    private int age;
    @Field
    private Double salary;
    @Field
    private Date birthday;

    public <T> EmployeeVO buildVO(Employee employee, Class<T> clazz, String json, ObjectMapper objectMapper) {
        EmployeeVO<T> objectEmployeeVO = new EmployeeVO<>();
        BeanUtils.copyProperties(employee,objectEmployeeVO);
        try {
            objectEmployeeVO.setT(objectMapper.readValue(json,clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return objectEmployeeVO;
    }
}
