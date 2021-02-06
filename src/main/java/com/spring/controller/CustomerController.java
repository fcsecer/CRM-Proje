package com.spring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.entity.Customer;
import com.spring.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	// Baþlangýç baðlayýcýlarý ekleniyor
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class,stringTrimmerEditor);
	}
	
	// müþteri listesi için yapýlandýrma yazýlýyor
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/list")
	public String listCustomers(Model theModel) {
		
		// Müþteri verilerini getirme
		List<Customer> theCustomers = customerService.getCustomers();
				
		// Müþteri modeli ekleniyor
		theModel.addAttribute("customers", theCustomers);
		
		return "list-customers";
	}
	
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		
		// form verilerini baðlamak için model özelliði oluþturuluyor
		Customer theCustomer = new Customer();
		
		theModel.addAttribute("customer", theCustomer);
		
		return "customer-form";
	}
	
	@PostMapping("/saveCustomer")
	public String saveCustomer(@Valid @ModelAttribute("customer") Customer theCustomer,
			BindingResult theBindingResult) {
		
		if(theBindingResult.hasErrors()) {
			return "customer-form";
		}
		else {
			// Serivisi kullanarak Müþteriler kaydedilir
			customerService.saveCustomer(theCustomer);	
			
			return "redirect:/customer/list";
		}
		
	}
	
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("customerId") int theId,
									Model theModel) {
		
		// Servisten müþteri getiriliyor
		Customer theCustomer = customerService.getCustomer(theId);	
		
		// formu önceden doldurmak için müþteriyi model özelliði olarak ayarlanýr
		theModel.addAttribute("customer", theCustomer);
		
		// form gönderiliyor		
		return "customer-form";
	}
	
	@GetMapping("/delete")
	public String deleteCustomer(@RequestParam("customerId") int theId) {
		
		// Müþteri silme
		customerService.deleteCustomer(theId);
		
		return "redirect:/customer/list";
	}
	
	@RequestMapping(value = "deleteSelected", method = RequestMethod.POST)
	public String deleteSelected(HttpServletRequest request, ModelMap modelMap){
		try {
			if(request.getParameterValues("customerId") != null) {
				for(String id : request.getParameterValues("customerId")) {
					int theId = Integer.parseInt(id);
					customerService.deleteCustomer(theId);
				}
			}
			return "redirect:/customer/list";
		} catch(Exception e) {
			modelMap.put("error", e.getMessage());
			return "redirect:/customer/list";
		}
	}
	
	
	@PostMapping("/search")
    public String searchCustomers(@RequestParam("theCustomerName") String theCustomerName,
                                    Model theModel) {

        // Müþteri arama
        List<Customer> theCustomers = customerService.searchCustomers(theCustomerName);
                
        // müþterileri modele ekleniyor
        theModel.addAttribute("customers", theCustomers);

        return "list-customers";        
    }
}