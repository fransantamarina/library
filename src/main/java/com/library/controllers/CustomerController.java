package com.library.controllers;

import com.library.entities.Customer;
import com.library.entities.Loan;
import com.library.services.CustomerService;
import com.library.services.LoanService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class CustomerController {

    private final CustomerService customerService;
    private final LoanService loanService;

    @Autowired
    public CustomerController(CustomerService customerService, LoanService loanService) {
        this.customerService = customerService;
        this.loanService = loanService;
    }

    @GetMapping
    public String listAuthors(ModelMap model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "/customers/list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap model, RedirectAttributes attr) {
        if (id == null) {
            model.addAttribute("customer", new Customer());
            return "/customers/form";
        } else {
            try {
                Customer customer = customerService.findById(id);
                model.addAttribute("customer", customer);
                return "/customers/form";
            } catch (Exception e) {
                attr.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/clientes";
            }
        }
    }

    @PostMapping("/form")
    public String saveCustomer(@ModelAttribute Customer customer, ModelMap model) {
        try {
            customerService.save(customer);
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/customers/form";
        }
    }

    @GetMapping("/alta/{id}")
    public String activate(@PathVariable String id, RedirectAttributes attr) {
        try {
            customerService.activate(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @GetMapping("/baja/{id}")
    public String deactivate(@PathVariable String id, RedirectAttributes attr) {
        try {
            customerService.deactivate(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @GetMapping("/search")
    public String findCustomerByKeyword(ModelMap model, @Param("keyword") String keyword) {
        model.addAttribute("customers", customerService.find(keyword));
        return "/customers/list";
    }

    @GetMapping("/perfil")
    public String showProfile(@RequestParam("customerId") String customerId, ModelMap model) {
        try {
            Customer customer = customerService.findById(customerId);
            model.addAttribute("customer", customer);

            //get this customers loans
            List<Loan> loans = loanService.getAllByCustomerId(customer.getId());
            model.addAttribute("loans", loans);

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "customers/profile";
    }

}
