package com.library.repositories;

import com.library.entities.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {

    public List<Author> findByNameContaining(String name);

    public List<Author> findByActiveTrue();
}
