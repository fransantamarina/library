package com.library.controllers;

import com.library.entities.Author;
import com.library.services.AuthorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/autores")
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String listAuthors(ModelMap model) {
        List<Author> authors = authorService.getAll();
        model.addAttribute("authors", authors);
        return "authors/author-list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap model, RedirectAttributes attr) {

        if (id == null) {
            model.addAttribute("author", new Author());
        } else {
            try {
                Author author = authorService.findById(id);
                model.addAttribute("author", author);
            } catch (Exception e) {
                attr.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/autores";
            }
        }
        return "/authors/author-form";
    }

    @PostMapping("/form")
    public String saveAuthor(@ModelAttribute Author author, ModelMap model, RedirectAttributes attr) {
        try {
            authorService.save(author);
            return "redirect:/autores";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("author", author);
            return "/authors/author-form";
        }
    }

    @GetMapping("/alta/{id}")
    public String activate(@PathVariable String id, RedirectAttributes attr) {
        try {
            authorService.activate(id);
            return "redirect:/autores";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/autores";
        }
    }

    @GetMapping("/baja/{id}")
    public String deactivate(@PathVariable String id, RedirectAttributes attr) {
        try {
            authorService.deactivate(id);
            return "redirect:/autores";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/autores";
        }
    }

    @GetMapping("/search")
    public String findBookByName(ModelMap model, @Param("name") String name) {
        model.addAttribute("authors", authorService.findByName(name));
        return "/authors/author-list";
    }

}
