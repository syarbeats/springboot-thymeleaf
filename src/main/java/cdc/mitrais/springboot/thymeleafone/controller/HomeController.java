package cdc.mitrais.springboot.thymeleafone.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cdc.mitrais.springboot.thymeleafone.config.EmployeeUserDetails;
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
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/editEmployee")
	public String editEmployee(@RequestParam(name="id") int id, Model model) {
		
		Employee employee = this.getEmployeeService().getEmployeeById(id);
		model.addAttribute("employee", employee);
		
		return "edit-employee";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = EmployeeViewURI.VIEW_ADD_EMPLOYE, method = RequestMethod.POST)
	public ModelAndView addEmployeeData(@ModelAttribute Employee employee) {
		
		logger.debug("Employee Name: "+employee.getName());
		this.getEmployeeService().addEmployee(employee);
		ModelAndView model = new ModelAndView("display-employees");
		model.addObject("employeeList", this.getEmployeeService().getEmployeeListWithoutPaging());
		return model;
	
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = EmployeeViewURI.VIEW_ADD_EMPLOYE, method = RequestMethod.GET)
	public String addEmployeeForm(Model model) {
		model.addAttribute("employee", new Employee());
		return "add-employee";
	}
	
	@RequestMapping(value = EmployeeViewURI.VIEW_HOME)
	public String home(Model model, HttpSession session) {
		
		String username = this.getCurrentUsername();
		
		/*Get Current login credential*/
		/*Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}*/
		
		model.addAttribute("message", "Gutten Morgen Brocks..");
		model.addAttribute("username", username);
		model.addAttribute("environment", "prod");
		
		if(username.equals("admin"))
			model.addAttribute("isAdmin", true);
		else
			model.addAttribute("isAdmin", false);
		
		return "welcome";
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
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = EmployeeRestURI.UPDATE_EMPLOYEE, method = RequestMethod.POST)
	public @ResponseBody Employee updateEmployee(@RequestBody Employee emp){
		logger.info("Update Employee...");
		return getEmployeeService().updateEmployeeSalary(emp);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = EmployeeRestURI.ADD_EMPLOYEE, method = RequestMethod.POST)
	public @ResponseBody Employee addEmployee(@RequestBody Employee emp){
		logger.info("Add New Employee...");
		return getEmployeeService().addEmployee(emp);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	
	
	@RequestMapping(value = EmployeeViewURI.VIEW_SHOW_EMPLOYE, method = RequestMethod.GET)
	public ModelAndView  showEmployee() {
		ModelAndView model = new ModelAndView("display-employees");
		model.addObject("employeeList", this.getEmployeeService().getEmployeeListWithoutPaging());
		return model;
	}
	
	 
	public String getCurrentUsername() {
		 
		 String username;
		 Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 
		 if (principal instanceof UserDetails) {
		   username = ((UserDetails)principal).getUsername();
		 } else {
		   username = principal.toString();
		 }
		 
		 return username;
	 }
	
	public EmployeeServices getEmployeeService() {
		return employeeService;
	}

	@Autowired
	public void setEmployeeService(EmployeeServices employeeService) {
		this.employeeService = employeeService;
	}
	
}
