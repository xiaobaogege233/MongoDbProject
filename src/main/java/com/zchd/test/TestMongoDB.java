package com.zchd.test;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.zchd.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

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

    // 排序
    @Test
    public void sort(){
        // 根据年龄排序
        Query query = new Query();
        // 倒序
        query.with(Sort.by(Sort.Order.desc("age")));
        List<Employee> descList = mongoTemplate.find(query, Employee.class);
        log.info("根据据年龄倒序排序结果:{}",descList);
        // 升序
        query = new Query();
        query.with(Sort.by(Sort.Order.asc("age")));
        List<Employee> ascList = mongoTemplate.find(query, Employee.class);
        log.info("根据据年龄升序排序结果:{}",ascList);
    }

    // 分页
    @Test
    public void limit(){
        long count = mongoTemplate.count(new Query(), Employee.class);
        for (long i = 0; i < count; i += 2) {
            Query query = new Query();
            query.skip(i);
            query.limit(2);
            List<Employee> employees = mongoTemplate.find(query, Employee.class);
            log.info("limit {},{} 的结果集:{}",i,2,employees);
        }
    }

    // 更新一条数据
    @Test
    public void updateFirst(){
        // 年龄大于100岁的第一位员工涨100
        Query query = new Query(Criteria.where("age").gt(100));

        Update update = new Update();
        update.inc("salary",100.0);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Employee.class);
        log.info("更新第一条结果:{}",updateResult.wasAcknowledged());
    }

    // 更新符合条件的所有
    @Test
    public void updateMulti(){
        // 年龄大于500岁的所有员工涨100
        Query query = new Query(Criteria.where("age").gt(500));

        Update update = new Update();
        update.inc("salary",100.0);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Employee.class);
        log.info("更新符合所有条件的结果:{}",updateResult.wasAcknowledged());
    }

    // 没有符合条件的则插入
    @Test
    public void upsert(){
        // 年龄大于500岁的所有员工涨100
        Query query = new Query(Criteria.where("name").is("希儿"));

        Update update = new Update();
        update.set("salary",55.0);

        UpdateResult updateResult = mongoTemplate.upsert(query, update, Employee.class);
        log.info("更新插入结果:{}",updateResult.wasAcknowledged());
    }

    // 删除数据
    @Test
    public void delete(){
        // 年龄大于500岁的所有员工涨100
        DeleteResult deleteResult = mongoTemplate.remove(new Query(Criteria.where("name").is("希儿")), Employee.class);
        log.info("删除结果:{}",deleteResult.wasAcknowledged());
    }

    // 年龄求和
    @Test
    public void aggregateSum(){
        // 年龄求和
        // 聚合 可能不止一个
        GroupOperation groupOperation = Aggregation.group().sum("age").as("sum");
        // 按顺序组装聚合
        TypedAggregation<Employee> employeeTypedAggregation = Aggregation.newAggregation(Employee.class, groupOperation);
        // 执行聚合 后面的映射可以用实体类
        AggregationResults<Map> aggregate = mongoTemplate.aggregate(employeeTypedAggregation, Map.class);
        log.info("年龄总和:{}",aggregate.getMappedResults());
    }
}
