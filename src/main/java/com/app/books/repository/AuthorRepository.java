package com.app.books.repository;

import com.app.books.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository <Author, Long>{
    @Query("SELECT DISTINCT a FROM Author a")
    List<Author> getAllAuthors();
    @Query("SELECT DISTINCT a FROM Author a WHERE deathYear >= 1925")
    List<Author> getAllAuthorsAliveAfter1925();
    @Query("SELECT a FROM Author a WHERE REPLACE(UPPER(a.name), ',', '') = REPLACE(UPPER(:authorName), ',', '')")
    Optional<Author> findByName(String authorName);

}
