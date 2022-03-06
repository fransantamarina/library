package com.library.controllers;

import com.library.entities.Customer;
import com.library.services.CustomerService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {

    private final CustomerService customerService;

    @Autowired
    public MainController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, ModelMap model) {
        if (null != error) {
            model.put("error", "Email o password inválidos");
        }
        return "/auth/login";
    }

    @GetMapping("/registro")
    public String signUpForm(ModelMap model) {
        model.addAttribute("customer", new Customer());

        return "/auth/signup-form";
    }

    @PostMapping("/registro")
    public String processSignUp(@Valid @ModelAttribute Customer customer, BindingResult result, ModelMap model) throws Exception {
        if (result.hasErrors()) {
            return "redirect:/registro";
        }
        model.addAttribute("customer", customerService.save(customer));

        return "/auth/login";
    }
}
