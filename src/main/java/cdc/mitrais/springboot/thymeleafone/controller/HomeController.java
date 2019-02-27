package cdc.mitrais.springboot.thymeleafone.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cdc.mitrais.springboot.thymeleafone.model.Employee;
import cdc.mitrais.springboot.thymeleafone.services.EmployeeServices;

@Controller
public class HomeController {

	private final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private EmployeeServices employeeService;
	
	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}
	
	@RequestMapping(value = EmployeeRestURI.GET_ALL_EMPLOYEE, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllEmployeesWithoutPaging(){
		
		logger.info("Getting all employees data....");
		List<Employee> empList;
		
		try{
			empList = this.getEmployeeService().getEmployeeListWithoutPaging();
		}catch(Exception e)
		{
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(empList, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = EmployeeRestURI.GET_EMPLOYEE_LIST, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllEmployees(@RequestParam(name="page") int page, @RequestParam(name="size") int size){
		
		logger.info("Getting all employees data....");
		List<Employee> empList;
		
		try{
			empList = getEmployeeService().getEmployeeList(page, size);
		}catch(Exception e)
		{
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity(empList, HttpStatus.OK);
		
	}

	@RequestMapping(value = EmployeeRestURI.GET_EMPLOYEE_BY_ID, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?>  getEmployeeById(@PathVariable("id") int id) {
		logger.info("Get Employee Data for Id: "+id);
		
		Employee emp;
		
		try{
			emp = getEmployeeService().getEmployeeById(id);
		}catch(Exception e)
		{
			return new ResponseEntity<String>("Data Employe dengan Id "+id+" tidak ditemukan",HttpStatus.NOT_FOUND);
		
		}
		return new ResponseEntity(emp, HttpStatus.OK);
	}
	
	@RequestMapping(value = EmployeeRestURI.GET_EMPLOYEE, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEmployee(@RequestParam(name="id") int id){
		logger.info("Get Employee Data for Id: "+id);
		Employee emp;
		
		try{
			emp = getEmployeeService().getEmployeeById(id);
		}catch(Exception e)
		{
			return new ResponseEntity<String>("Data Employe dengan Id "+id+" tidak ditemukan",HttpStatus.NOT_FOUND);
		
		}
		return new ResponseEntity(emp, HttpStatus.OK);
	}
	
	@RequestMapping(value = EmployeeRestURI.UPDATE_EMPLOYEE, method = RequestMethod.POST)
	public @ResponseBody Employee updateEmployee(@RequestBody Employee emp){
		logger.info("Update Employee...");
		return getEmployeeService().updateEmployeeSalary(emp);
	}
	
	@RequestMapping(value = EmployeeRestURI.ADD_EMPLOYEE, method = RequestMethod.POST)
	public @ResponseBody Employee addEmployee(@RequestBody Employee emp){
		logger.info("Add New Employee...");
		return getEmployeeService().addEmployee(emp);
	}
	
	@RequestMapping(value = EmployeeRestURI.DELETE_EMPLOYEE, method = RequestMethod.DELETE)
	public @ResponseBody String deleteEmployee(@PathVariable("id") int id){
		logger.info("Delete Employee with Id: "+id);
		String response;
		
		try {
			Employee emp = getEmployeeService().getEmployeeById(id);
			getEmployeeService().deleteEmployee(emp);
			response = "Data with Id: "+emp.getId() + " has been deleted successfully...";
		}catch(Exception e) {
			response = e.getMessage();
		}
		
		return response;
	}
	
	public EmployeeServices getEmployeeService() {
		return employeeService;
	}

	@Autowired
	public void setEmployeeService(EmployeeServices employeeService) {
		this.employeeService = employeeService;
	}
	
	@RequestMapping(value = EmployeeViewURI.VIEW_SHOW_EMPLOYE, method = RequestMethod.GET)
	public ModelAndView  showEmployee() {
		ModelAndView model = new ModelAndView("display-employees");
		model.addObject("employeeList", this.getEmployeeService().getEmployeeListWithoutPaging());
		return model;
	}
}
