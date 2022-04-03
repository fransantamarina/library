package com.library.services;

import com.library.enums.Role;
import com.library.entities.Customer;
import com.library.repositories.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CustomerService implements UserDetailsService {

    public final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(rollbackOn = {Exception.class})
    public Customer save(Customer customer) throws Exception {
        validate(customer);
        activateIfNew(customer);
        customer.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        return customerRepository.save(customer);
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

    @Transactional
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
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
            customer.setRole(Role.USER);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            //return null;
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        List<GrantedAuthority> permissions = new ArrayList<>();
        GrantedAuthority rolePermissions = new SimpleGrantedAuthority("ROLE_" + customer.getRole().toString());
        permissions.add(rolePermissions);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpSession session = attr.getRequest().getSession(true);

        session.setAttribute("customersession", customer);

        return new User(customer.getEmail(), customer.getPassword(), permissions);
    }

}
