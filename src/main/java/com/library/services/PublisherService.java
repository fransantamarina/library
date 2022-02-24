package com.library.services;

import com.library.entities.Publisher;
import com.library.repositories.PublisherRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    public final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(rollbackOn = {Exception.class})
    public void save(Publisher publisher) throws Exception {
        validate(publisher);
        activateIfNew(publisher);
        publisherRepository.save(publisher);
    }

    @Transactional
    public Publisher findById(String id) throws Exception {
        return publisherRepository.findById(id).orElseThrow(() -> new Exception("No se encontro la editorial"));
    }

    @Transactional
    public List<Publisher> getAll() {
        return publisherRepository.findAll();
    }

    @Transactional
    public List<Publisher> getAllActive() {
        return publisherRepository.findByActiveTrue();
    }

    @Transactional
    public List<Publisher> findByName(String name) {
        return publisherRepository.findByName(name);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void activate(String id) throws Exception {
        Publisher publisher = this.findById(id);
        publisher.setActive(true);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deactivate(String id) throws Exception {
        Publisher publisher = this.findById(id);
        publisher.setActive(false);
    }

    private void validate(Publisher publisher) throws Exception {
        boolean publisherExists = this.getAll().stream()
                .anyMatch(existingPublisher -> existingPublisher.getName().equalsIgnoreCase(publisher.getName()));

        if (publisherExists && publisher.getId().isEmpty()) {
            throw new Exception("Ya existe una editorial con ese nombre");
        }

        if (publisher.getName() == null || publisher.getName().trim().isEmpty()) {
            throw new Exception("Debe indicar el nombre de la editorial");
        }

        if (publisher.getName().length() < 5) {
            throw new Exception("El nombre de la editorial debe teber por lo menos 5 caracteres");
        }
    }

    private void activateIfNew(Publisher publisher) {
        if (publisher.getActive() == null) {
            publisher.setActive(true);
        }
    }

}
