package com.spring.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.user.CrmUser;

@Controller
@RequestMapping("/register")
public class RegistrationController {
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private Map<String, String> roles;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}	
	
	@PostConstruct
	protected void loadRoles() {
		
		// Hashmap kullanarak, bu bilgiyi bir veritabanýndan da okunabilir hale getiriyoruz
		
		roles = new LinkedHashMap<String, String>();
		
		
		roles.put("ROLE_EMPLOYEE", "Employee");
		//roles.put("ROLE_MANAGER", "Manager");
		//roles.put("ROLE_ADMIN", "Admin");		
	}
	
	@GetMapping("/showRegistrationForm")
	public String showMyLoginPage(Model theModel) {
		
		theModel.addAttribute("crmUser", new CrmUser());
		
		// Rollerin formda görünmesi için
		theModel.addAttribute("roles", roles);
				
		return "registration-form";
		
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(
				@Valid @ModelAttribute("crmUser") CrmUser theCrmUser, 
				BindingResult theBindingResult, 
				Model theModel) {
						
		String userName = theCrmUser.getUserName();
		
		logger.info("Processing registration form for: " + userName);
		
		// form doðrulama yapýlýyor
		if (theBindingResult.hasErrors()) {

			theModel.addAttribute("crmUser", new CrmUser());
			
			// form üzerinde rollerin seçilmesi için
			theModel.addAttribute("roles", roles);
						
			theModel.addAttribute("registrationError", "User name/password can not be empty.");

			logger.warning("User name/password can not be empty.");
			
			return "registration-form";	
		}
		
		// Kullanýcý veritabanýnda bulunuyor mu?Kontrol yapýlýyor
		boolean userExists = doesUserExist(userName);
		
		if (userExists) {
			theModel.addAttribute("crmUser", new CrmUser());
			theModel.addAttribute("registrationError", "User name already exists.");

			logger.warning("User name already exists.");
			
			return "registration-form";			
		}
		
		// Þifre encrypt haline getiriliyor
        String encodedPassword = passwordEncoder.encode(theCrmUser.getPassword());

        // encoding algoritmasý id ile eþleþiyor
        encodedPassword = "{bcrypt}" + encodedPassword;
                 
		// Eðer rol seçilmez ise Employee rolü veriliyor
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_EMPLOYEE");

        // kullanýcý nesnesi oluþtur ( Spring Security framework kullanarak)
        User tempUser = new User(userName, encodedPassword, authorities);

        // Kullanýcý veritabanýna kayýt ediliyor
        userDetailsManager.createUser(tempUser);		
		
        logger.info("Successfully created user: " + userName);
        
        return "registration-confirmation";		
	}
	
	private boolean doesUserExist(String userName) {
		
		logger.info("Checking if user exists: " + userName);
		
		// Kullanýcý veritabanýna önceden kayýtlýmý kontrol ediliyor
		boolean exists = userDetailsManager.userExists(userName);
		
		logger.info("User: " + userName + ", exists: " + exists);
		
		return exists;
	}

}