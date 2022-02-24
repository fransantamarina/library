package com.library.controllers;

import com.library.entities.Book;
import com.library.entities.Customer;
import com.library.entities.Loan;
import com.library.services.BookService;
import com.library.services.CustomerService;
import com.library.services.LoanService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prestamos")
public class LoanController {

    private final LoanService loanService;
    private final CustomerService customerService;
    private final BookService bookService;

    @Autowired
    public LoanController(LoanService loanService, CustomerService customerService, BookService bookService) {
        this.loanService = loanService;
        this.customerService = customerService;
        this.bookService = bookService;
    }

    @GetMapping
    public String listLoans(ModelMap model) {

        model.addAttribute("loans", loanService.getAll());
        return "/loans/loan-list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam String customerId, ModelMap model, RedirectAttributes attr) {

        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        model.addAttribute("customerId", customerId);

        if (!model.containsKey("loan")) {
            model.addAttribute("loan", new Loan());
            return "/loans/loan-form";
        }

        Loan loan = (Loan) model.getAttribute("loan");

        if (loan != null && !loan.getId().isEmpty()) {
            try {
                Customer customer = customerService.findById(customerId);
                loan = loanService.findById(loan.getId());
                loan.setCustomer(customer);
                model.addAttribute("loan", loan);
            } catch (Exception e) {
                System.out.println("Error en GetMapping : " + e.getMessage());
                model.addAttribute("errorMessage", e.getMessage());
            }
        }
        return "/loans/loan-form";
    }

    @PostMapping("/form")
    public String saveLoan(@ModelAttribute Loan loan, ModelMap model, @RequestParam String customerId, RedirectAttributes attr) {
        try {

            Customer customer = customerService.findById(customerId);
            loan.setCustomer(customer);
            loanService.save(loan);
            return "redirect:/prestamos";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("loan", loan);
            model.addAttribute("books", bookService.getAll());
            return "/loans/loan-form";
        }
    }

    @GetMapping("/alta/{id}")
    public String activate(@PathVariable String id, RedirectAttributes attr) {
        try {
            loanService.activate(id);
            return "redirect:/prestamos";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/prestamos";
        }
    }

    @GetMapping("/baja/{id}")
    public String deactivate(@PathVariable String id, RedirectAttributes attr) {
        try {
            loanService.deactivate(id);
            return "redirect:/prestamos";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/prestamos";
        }
    }

    @GetMapping("/search")
    public String findBookByName(ModelMap model, @Param("keyword") String keyword) {
        model.addAttribute("loans", loanService.find(keyword));
        return "/loans/loan-list";
    }

}
