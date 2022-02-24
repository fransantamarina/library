package com.library.services;

import com.library.entities.Book;
import com.library.entities.Loan;
import com.library.repositories.LoanRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    public final LoanRepository loanRepository;
    public final CustomerService customerService;
    public final BookService bookService;

    @Autowired
    public LoanService(LoanRepository loanRepository, CustomerService customerService, BookService bookService) {
        this.loanRepository = loanRepository;
        this.customerService = customerService;
        this.bookService = bookService;
    }

    @Transactional(rollbackOn = {Exception.class})
    public void save(Loan loan) throws Exception {
        validate(loan);
        activateIfNew(loan);
        checkBooks(loan.getBooks());
        loanRepository.save(loan);
    }

    @Transactional
    public Loan findById(String id) throws Exception {
        return loanRepository.findById(id).orElseThrow(() -> new Exception("No se encontro el cliente"));
    }

    @Transactional
    public List<Loan> getAll() {
        return checkExpired();
    }

    @Transactional
    public List<Loan> getAllActive() {
        return loanRepository.findByActiveTrue();
    }

    @Transactional
    public List<Loan> find(String keyword) {
        // Look for tidier way of formatting date
        if (keyword.contains("-") && keyword.length() == 5) {
            keyword = keyword.substring(3) + "-" + keyword.substring(0, 2);
        }
        System.out.println("KEYWORD = " + keyword);
        return loanRepository.find(keyword);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void activate(String id) throws Exception {
        Loan loan = this.findById(id);

        if (loan.isExpired()) {
            throw new Exception("El prestamo que intenta dar de alta llego a su fecha de finalizacion");
        }
        loan.setActive(true);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deactivate(String id) throws Exception {
        Loan loan = this.findById(id);

        loan.getBooks().forEach(book -> bookService.returnBook(book));
        loan.setActive(false);
    }

    private void validate(Loan loan) throws Exception {

        if (loan.getCustomer() == null) {
            throw new Exception("Seleccione un cliente e intente nuevamente");
        }

        boolean customerHasLoan = getAll().stream().anyMatch(existingLoan
                -> existingLoan.getCustomer().equals(loan.getCustomer()) && existingLoan.getActive());

        if (customerHasLoan) {
            throw new Exception("El cliente seleccionado ya tiene un prestamo a su nombre");
        }

        if (loan.getBooks() == null || loan.getBooks().isEmpty()) {
            throw new Exception("Por favor, seleccione al menos un libro");
        }

        for (Book book : loan.getBooks()) {
            if (!book.isAvailable()) {
                throw new Exception("No hay mas ejemplares disponibles de : " + book.getTitle());
            }
        }

        if (loan.getStartDate() == null || loan.getEndDate() == null) {
            throw new Exception("Debe indicar una fecha de inicio y fin para el prestamo");
        }

        if (loan.getStartDate().isAfter(loan.getEndDate())) {
            throw new Exception("La fecha de inicio no puede ser posterior a la de fin");
        }

        if (loan.getStartDate().isBefore(LocalDate.now())) {
            throw new Exception("El prestamo no puede iniciar en el pasado");
        }

        if (loan.getStartDate().plusDays(15).isBefore(loan.getEndDate())) {
            throw new Exception("El prestamo puede ser por un maximo de 15 dias");
        }

    }

    private void activateIfNew(Loan loan) {
        if (loan.getActive() == null) {
            loan.setActive(true);
        }
    }

    private List<Loan> checkExpired() {
        List<Loan> loans = loanRepository.findAll();
        for (Loan loan : loans) {
            if (loan.isExpired()) {
                loan.setActive(false);
            }
        }
        return loans;

    }

    private void checkBooks(Set<Book> books) throws Exception {
        for (Book book : books) {
            bookService.loan(book);
        }
    }

}
