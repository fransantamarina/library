package com.library.controllers;

import com.library.entities.Publisher;
import com.library.services.PublisherService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/editoriales")
public class PublisherController {

    private final PublisherService publisherService;

    @Autowired
    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public String listAuthors(ModelMap model) {
        List<Publisher> publishers = publisherService.getAll();
        model.addAttribute("publishers", publishers);
        return "/publishers/list";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String id, ModelMap model, RedirectAttributes attr) {
        if (id == null) {
            model.addAttribute("publisher", new Publisher());
            return "/publishers/form";
        } else {
            try {
                Publisher publisher = publisherService.findById(id);
                model.addAttribute("publisher", publisher);
                return "/publishers/form";
            } catch (Exception e) {
                attr.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/editoriales";
            }
        }
    }

    @PostMapping("/form")
    public String saveLoan(@ModelAttribute Publisher publisher, ModelMap model) {
        try {
            publisherService.save(publisher);
            return "redirect:/editoriales";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/publishers/form";
        }
    }

    @GetMapping("/alta/{id}")
    public String activate(@PathVariable String id, RedirectAttributes attr) {
        try {
            publisherService.activate(id);
            return "redirect:/editoriales";
        } catch (Exception e) {
            attr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/editoriales";
        }
    }

    @GetMapping("/baja/{id}")
    public String deactivate(@PathVariable String id, RedirectAttributes attr) {
        try {
            publisherService.deactivate(id);
            return "redirect:/editoriales";
        } catch (Exception e) {
            attr.addFlashAttribute("editoriales", e.getMessage());
            return "redirect:/editoriales";
        }
    }

    @GetMapping("/search")
    public String findPublisherByName(ModelMap model, @Param("name") String name) {
        model.addAttribute("publishers", publisherService.findByName(name));
        return "/publishers/list";
    }

}
