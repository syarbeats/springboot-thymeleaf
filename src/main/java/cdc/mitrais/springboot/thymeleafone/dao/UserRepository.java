package cdc.mitrais.springboot.thymeleafone.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cdc.mitrais.springboot.thymeleafone.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {
	
	User findByUsername(String username);
}
