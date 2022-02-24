package com.library.services;

import com.library.entities.Customer;
import com.library.repositories.CustomerRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    public final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(rollbackOn = {Exception.class})
    public void save(Customer customer) throws Exception {
        validate(customer);
        activateIfNew(customer);
        customerRepository.save(customer);
    }

    @Transactional
    public Customer findById(String id) throws Exception {
        return customerRepository.findById(id).orElseThrow(() -> new Exception("No se encontro el cliente"));
    }

    @Transactional
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public List<Customer> getAllActive() {
        return customerRepository.findByActiveTrue();
    }

    @Transactional
    public List<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }

    @Transactional
    public List<Customer> find(String keyword) {
        return customerRepository.find(keyword);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void activate(String id) throws Exception {
        Customer customer = this.findById(id);
        customer.setActive(true);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deactivate(String id) throws Exception {
        Customer customer = this.findById(id);
        customer.setActive(false);
    }

    private void validate(Customer customer) throws Exception {

        if (customer.getName() == null || customer.getLastName() == null || customer.getName().trim().length() < 3 || customer.getLastName().trim().length() < 3) {
            throw new Exception("Por favor, revise que el nombre y apellido tengan por lo menos 3 caracteres");
        }

        boolean phoneNumberExists = getAll().stream()
                .anyMatch(existingCustomer -> existingCustomer.getPhoneNumber().equals(customer.getPhoneNumber()));

        if (phoneNumberExists) {
            throw new Exception("Ya existe un cliente registrado con ese telefono");
        }

        boolean idNumberExists = getAll().stream().anyMatch(existingCustomer -> existingCustomer.equals(customer));

        if (idNumberExists && customer.getId().isEmpty()) {
            throw new Exception("Ya existe un cliente registrado con ese DNI");
        }

        int phoneNumberLength = customer.getPhoneNumber().trim().length();
        if (phoneNumberLength < 9 || phoneNumberLength > 12) {
            throw new Exception("Por favor, revise que el telefono tenga por lo menos 9 y como maximo 12 caracteres");
        }

        if (customer.getIdNumber().trim().isEmpty()) {
            throw new Exception("Debe ingresar el DNI");
        }

        if (customer.getIdNumber().length() != 7 && customer.getIdNumber().length() != 8) {
            throw new Exception("DNI invalido");
        }

    }

    private void activateIfNew(Customer customer) {
        if (customer.getActive() == null) {
            customer.setActive(true);
        }
    }

}
