package com.app.books.repository;

import com.app.books.model.Book;
import com.app.books.model.DatosBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);

}
