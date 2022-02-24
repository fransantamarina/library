package com.library.services;

import com.library.entities.Book;
import com.library.repositories.BookRepository;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(rollbackOn = {Exception.class})
    public void save(Book book) throws Exception {
        validate(book);
        activateIfNew(book);
        bookRepository.save(book);
    }

    @Transactional
    public Book findById(String id) throws Exception {
        return bookRepository.findById(id).orElseThrow(() -> new Exception("No se encontro el libro"));
    }

    @Transactional
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Transactional
    public List<Book> getAllActive() {
        return bookRepository.findByActiveTrue();
    }

    @Transactional
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Transactional
    public List<Book> find(String keyword) {
        return bookRepository.find(keyword);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void activate(String id) throws Exception {
        Book book = findById(id);
        book.setActive(true);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deactivate(String id) throws Exception {
        Book book = findById(id);
        book.setActive(false);
    }

    private void validate(Book book) throws Exception {

        boolean bookExists = this.getAll().stream().anyMatch(existingBook -> existingBook.equals(book));

        //Added empty check for when a book is being created is the only time when id is Empty
        if (bookExists && book.getId().isEmpty()) {
            throw new Exception("El ISBN ingresado ya se encuentra en la base de datos");
        }

        if (book.getTitle().trim().length() < 5) {
            throw new Exception("El titulo del libro debe tener 5 caracteres como minimo");
        }

        int isbnLength = book.getIsbn().trim().length();
        if (isbnLength != 10 && isbnLength != 13) {
            throw new Exception("El ISBN debe tener 10 o 13 caracteres");
        }

        int currentYear = LocalDate.now().getYear();
        if (book.getYear() < 1455) {
            throw new Exception("El año del libro no puede ser anterior a 1455");
        }
        if (book.getYear() > currentYear) {
            throw new Exception("El año del libro no puede ser posterior a " + currentYear);
        }

        if (book.getCopies() == null || book.getCopies() < 0) {
            book.setCopies(0);
            throw new Exception("El numero de copias es invalido, por defecto sera 0");
        }

        if (book.getAuthor() == null) {
            throw new Exception("Debe seleccionar un autor, si no existe, debe cargarlo primero o hablar con administracion para que lo carguen");
        }

        if (book.getPublisher() == null) {
            throw new Exception("Debe seleccionar una editorial, si no existe, debe cargarla primero o hablar con administracion para que la carguen");
        }
    }

    public void loan(Book book) throws Exception {
        if (book.getCopies() == 1) {
            book.setActive(false);
        }
        if (book.getCopies() == 0) {
            throw new Exception("No hay copias disponibles de este libro");
        }
        book.setCopies(book.getCopies() - 1);
        book.setLoaned(book.getLoaned() + 1);

    }

    public void returnBook(Book book) {
        if (book.getCopies() == 0) {
            book.setActive(true);
        }
        book.setCopies(book.getCopies() + 1);
        book.setLoaned(book.getLoaned() - 1);
    }

    private void activateIfNew(Book book) {
        if (book.getActive() == null) {
            book.setActive(true);
        }
        if (book.getLoaned() == null) {
            book.setLoaned(0);
        }
    }

}
