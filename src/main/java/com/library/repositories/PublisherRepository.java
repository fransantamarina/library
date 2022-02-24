package com.library.repositories;

import com.library.entities.Publisher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    public List<Publisher> findByActiveTrue();

    @Query("SELECT p FROM Publisher p WHERE p.name LIKE %:name%")
    public List<Publisher> findByName(@Param("name") String name);

//  Commented out because it brings a Publisher only when name matches exactly
//  public List<Publisher> findByName(String name);
}
