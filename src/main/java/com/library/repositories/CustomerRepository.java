package com.library.repositories;

import com.library.entities.Book;
import com.library.entities.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:name%")
    public List<Customer> findByName(@Param("name") String name);

    public List<Customer> findByActiveTrue();

    @Query("Select c from Customer c where c.name LIKE %?1%"
            + "OR c.lastName LIKE %?1%"
            + "OR c.idNumber LIKE %?1%")
    public List<Customer> find(String keyword);

}
