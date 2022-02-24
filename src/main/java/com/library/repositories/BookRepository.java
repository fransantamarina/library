package com.library.repositories;

import com.library.entities.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    public List<Book> findByTitleContaining(String title);

    @Query("Select b from Book b where b.title LIKE %?1%"
            + "OR b.isbn LIKE %?1%"
            + "OR b.author.name LIKE %?1%")
    public List<Book> find(String keyword);

    public List<Book> findByActiveTrue();

}
