package com.zchd.test;

import com.zchd.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestMongoDB {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 使用insert进行保存，当id重复的时候，就会抛出异常
    @Test
    public void insert(){
        Employee employee = new Employee(5,"芙宁娜",500,5.0,new Date());
        Employee insert = mongoTemplate.insert(employee);
        log.info("插入成功:{}",insert);
    }

    // 使用save进行保存，当id重复的时候，就是更新，否则就是插入
    @Test
    public void save(){
        Employee employee = new Employee(1,"温迪",200,50.0,new Date());
        Employee insert = mongoTemplate.save(employee);
        log.info("保存成功:{}",insert);
    }

    // 使用insert进行批量插入
    @Test
    public void insertBatch(){
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(2,"钟离",6000,50.0,new Date()));
        employeeList.add(new Employee(3,"雷电将军",2000,50.0,new Date()));
        employeeList.add(new Employee(4,"纳西达",500,50.0,new Date()));
        Collection<Employee> employees = mongoTemplate.insert(employeeList, Employee.class);
        log.info("批量插入成功:{}",employees);
    }

    // 查询所有
    @Test
    public void findAll(){
        List<Employee> employees = mongoTemplate.findAll(Employee.class);
        log.info("查询全部成功:{}",employees);
    }

    // 查询第一条结果
    @Test
    public void findOne(){
        Employee employee = mongoTemplate.findOne(new Query(), Employee.class);
        log.info("查询第一条结果成功:{}",employee);
    }

    // 单条件查询
    @Test
    public void findByCriteria(){
        //查询查询姓名是纳西达的员工
        Query equalQuery = new Query(Criteria.where("name").is("纳西达"));
        //查询薪资大于10小于50的员工
        Query rangeQuery = new Query(Criteria.where("salary").gt(0).lt(51));
        //正则查询（模糊查询） java中正则不需要//
        Query likeQuery = new Query(Criteria.where("name").regex("电"));

        //执行Query
        List<Employee> equalQueryList = mongoTemplate.find(equalQuery, Employee.class);
        List<Employee> rangeQueryList = mongoTemplate.find(rangeQuery, Employee.class);
        List<Employee> likeQueryList = mongoTemplate.find(likeQuery, Employee.class);
        log.info("等值查询结果:{}",equalQueryList);
        log.info("范围查询结果:{}",rangeQueryList);
        log.info("模糊查询结果:{}",likeQueryList);
    }

    // 多条件查询
    @Test
    public void findByMultipleCriteria(){
        // and 查询姓名为雷电将军，薪资大于0的员工
        Criteria andCriteria = new Criteria();
        andCriteria.andOperator(Criteria.where("name").is("雷电将军"),Criteria.where("salary").gt(0));
        Query andQuery = new Query(andCriteria);
        List<Employee> employees = mongoTemplate.find(andQuery, Employee.class);
        log.info("and 的第一种写法:{}",employees);

        andQuery = new Query(Criteria.where("name").is("雷电将军").and("salary").gt(0));
        employees = mongoTemplate.find(andQuery, Employee.class);
        log.info("and 的第二种写法:{}",employees);

        // or 查询姓名等于纳西达或者工资大于50的员工
        Criteria orCriteria = new Criteria();
        orCriteria.orOperator(Criteria.where("name").is("纳西达"),Criteria.where("salary").gt(50));
        Query orQuery = new Query(orCriteria);
        List<Employee> employeeList = mongoTemplate.find(orQuery, Employee.class);
        log.info("or 的第一种写法:{}",employeeList);

        /*
         * 注意这种不等同于上面第一种写法 所以结果是不一样的
         * 上面的等同于 A or B
         * 下面的等同于 A and (B or C)  当没有C的时候就等同于 A and B
         */
        orQuery = new Query(Criteria.where("name").is("纳西达").orOperator(Criteria.where("salary").gt(50)));
        employeeList = mongoTemplate.find(orQuery, Employee.class);
        log.info("or 的第二种写法:{}",employeeList);
    }


}
