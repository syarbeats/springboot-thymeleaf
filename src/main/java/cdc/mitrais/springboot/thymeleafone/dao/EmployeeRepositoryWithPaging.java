package cdc.mitrais.springboot.thymeleafone.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cdc.mitrais.springboot.thymeleafone.model.Employee;


public interface EmployeeRepositoryWithPaging extends PagingAndSortingRepository<Employee, Integer>  {

}
