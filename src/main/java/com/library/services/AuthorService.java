package com.library.services;

import com.library.entities.Author;
import com.library.exceptions.WebException;
import com.library.repositories.AuthorRepository;
import java.io.FileReader;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    public final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(rollbackOn = {Exception.class, WebException.class})
    public void save(Author author) throws WebException {
        validate(author);
        activateIfNew(author);
        authorRepository.save(author);
    }

    @Transactional(rollbackOn = {Exception.class, WebException.class})
    public Author findById(String id) throws WebException {
        return authorRepository.findById(id).orElseThrow(() -> new WebException("No se encontro el autor"));
    }

    @Transactional
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    @Transactional
    public List<Author> getAllActive() {
        return authorRepository.findByActiveTrue();
    }

    @Transactional
    public List<Author> findByName(String name) {
        return authorRepository.findByNameContaining(name);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void activate(String id) throws Exception {
        Author author = this.findById(id);
        author.setActive(true);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deactivate(String id) throws Exception {
        Author author = this.findById(id);
        author.setActive(false);
    }

    private void validate(Author author) throws WebException {

        boolean authorExists = getAll().stream().anyMatch(existingAuthor -> existingAuthor.equals(author));

        if (authorExists && author.getId().isEmpty()) {
            throw new WebException("Ya existe un autor con ese nombre");
        }

        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new WebException("Debe indicar el nombre del autor");
        }

        if (author.getName().length() < 5) {
            throw new WebException("El nombre debe tener por lo menos 5 caracteres");
        }
    }

    private void activateIfNew(Author author) {
        if (author.getActive() == null) {
            author.setActive(true);
        }
    }

}
