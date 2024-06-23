package com.app.books.repository;

import com.app.books.model.Author;
import com.app.books.model.Book;
import com.app.books.model.DatosBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

     @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors")
     List<Book> getAllBooks();

     @Query("SELECT DISTINCT a FROM Author a")
     List<Author> getAllAuthors();
}
