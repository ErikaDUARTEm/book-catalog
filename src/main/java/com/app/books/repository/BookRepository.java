package com.app.books.repository;

import com.app.books.model.Author;
import com.app.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

     @Override
     List<Book> findAll();

     @Query("SELECT b FROM Book b WHERE :language IN (b.languages)")
     List<Book> findBookByLanguage(String language);

     @Query("SELECT DISTINCT b FROM Book b WHERE b.title = :title")
     Book findByTitle(String title);




}
