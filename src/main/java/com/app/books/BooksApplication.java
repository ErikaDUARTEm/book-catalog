package com.app.books;

import com.app.books.principal.Principal;
import com.app.books.repository.AuthorRepository;
import com.app.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooksApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BooksApplication.class, args);
	}

	@Autowired
	private BookRepository repository;

	@Autowired
	private AuthorRepository authorRepository;

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository, authorRepository);
		principal.viewMenu();
	}
}
