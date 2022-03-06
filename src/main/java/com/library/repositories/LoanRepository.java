package com.library.repositories;

import com.library.entities.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

    public List<Loan> findByActiveTrue();

    @Query("Select l from Loan l where l.customer.name LIKE %?1%"
            + "OR l.customer.lastName LIKE %?1%"
            + "OR l.customer.idNumber LIKE %?1%"
            + "OR l.startDate LIKE %?1%"
            + "OR l.endDate LIKE %?1%")
    public List<Loan> find(String keyword);

    public List<Loan> findByCustomerEmail(String customerEmail);

}
