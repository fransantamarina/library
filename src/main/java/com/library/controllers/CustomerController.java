package com.library.controllers;

import com.library.entities.Customer;
import com.library.services.CustomerService;
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

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listAuthors(ModelMap model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "/customers/customer-list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap model, RedirectAttributes attr) {
        if (id == null) {
            model.addAttribute("customer", new Customer());
            return "/customers/customer-form";
        } else {
            try {
                Customer customer = customerService.findById(id);
                model.addAttribute("customer", customer);
                return "/customers/customer-form";
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
            return "/customers/customer-form";
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
    public String findBookByName(ModelMap model, @Param("keyword") String keyword) {
        model.addAttribute("customers", customerService.find(keyword));
        return "/customers/customer-list";
    }

}
