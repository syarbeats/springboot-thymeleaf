package cdc.mitrais.springboot.thymeleafone.dao;

import org.springframework.data.repository.CrudRepository;

import cdc.mitrais.springboot.thymeleafone.model.Employee;


public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

}
