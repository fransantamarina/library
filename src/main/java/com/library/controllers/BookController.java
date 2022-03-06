package com.library.controllers;

import com.library.entities.*;
import com.library.services.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/libros")
@PreAuthorize("isAuthenticated()")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CustomerService customerService;

    @Autowired
    public BookController(BookService bookService, AuthorService authorService, PublisherService publisherService, CustomerService customerService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.customerService = customerService;
    }

    @GetMapping
    public String listBooks(ModelMap model) {
        List<Book> books = bookService.getAll();
        model.addAttribute("books", books);
        return "/books/book-list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String bookId, ModelMap model) {

        model.addAttribute("authors", authorService.getAll());
        model.addAttribute("publishers", publisherService.getAll());
        if (bookId == null) {
            model.addAttribute("book", new Book());
            return "/books/book-form";
        }

        if (!bookId.isEmpty()) {
            try {
                Book book = bookService.findById(bookId);
                model.addAttribute("book", book);
            } catch (Exception e) {
                System.out.println("Error en GetMapping : " + e.getMessage());
                model.addAttribute("errorMessage", e.getMessage());
            }
        }
        return "/books/book-form";
    }

    @PostMapping("/form")
    public String saveBook(@ModelAttribute Book book,
            @RequestParam String authorId,
            @RequestParam String publisherId,
            RedirectAttributes attr,
            ModelMap model
    ) {
        try {
            Author author = authorService.findById(authorId);
            Publisher publisher = publisherService.findById(publisherId);
            book.setAuthor(author);
            book.setPublisher(publisher);
            bookService.save(book);
            return "redirect:/libros";
        } catch (Exception e) {
            System.out.println("ERROR :" + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("book", book);
            model.addAttribute("authors", authorService.getAll());
            model.addAttribute("publishers", publisherService.getAll());
            return "/books/book-form";
        }
    }

    @GetMapping("/alta/{id}")
    public String activate(@PathVariable String id, RedirectAttributes attr
    ) {
        try {
            bookService.activate(id);
            return "redirect:/libros";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/libros";
        }
    }

    @GetMapping("/baja/{id}")
    public String deactivate(@PathVariable String id, RedirectAttributes attr
    ) {
        try {
            bookService.deactivate(id);
            return "redirect:/libros";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/libros";
        }
    }

    @GetMapping("/search")
    public String findBookByName(ModelMap model, @Param("keyword") String keyword) {
        model.addAttribute("books", bookService.find(keyword));
        return "/books/book-list";
    }

}
