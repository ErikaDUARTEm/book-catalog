package com.app.books.principal;


import com.app.books.model.Author;
import com.app.books.model.Book;
import com.app.books.model.Data;
import com.app.books.model.DatosBook;
import com.app.books.repository.AuthorRepository;
import com.app.books.repository.BookRepository;
import com.app.books.service.BookConverter;
import com.app.books.service.ConsumoApi;
import com.app.books.service.ConvierteDatos;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private BookRepository repository;
    private AuthorRepository authorRepository;

    public Principal(BookRepository repository, AuthorRepository authorRepository) {

        this.repository = repository;
        this.authorRepository = authorRepository;

    }

    public void viewMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1- Buscar libro
                    2- Listar libros buscados
                    3- Listar Autores
                    4- Listar todos los autores vivos despues de 1925
                    5- Listar los libros que están en español
                    6- Escribe el nombre del autor
                    0- Salir
                    """;
            System.out.println(menu);
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consume el salto de línea
                switch (opcion) {
                    case 1:
                        searchBook();
                        break;
                    case 2:
                        getAllBooks();
                        break;
                    case 3:
                        getAllAuthors();
                        break;
                    case 4:
                        getAllAuthorsAliveAfter1925();
                        break;
                    case 5:
                        findBookByLanguage();
                        break;
                    case 6:
                        findByAuthor();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, Introduce el nombre en mayuscula.");
                scanner.nextLine(); // Consume el valor incorrecto para evitar un bucle infinito
            }
        }
    }
    @Transactional
    private void searchBook() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nameBook = scanner.nextLine().toLowerCase();

        Book existingBook = repository.findByTitle(nameBook);
        if (existingBook != null) {
            System.out.println(existingBook);
        } else {
            var json = consumoApi.getDatos(URL_BASE + "?search=" + nameBook.replace(" ", "+"));
            Data datos = conversor.getDatos(json, Data.class);

            if (datos != null) {
                List<DatosBook> datosBooks = BookConverter.convertToDatosBookList(datos);
                datosBooks.stream()
                        .findFirst()
                        .ifPresent(datosBook -> {
                            Book newBook = new Book(datosBook);

                            if (newBook.getAuthors() != null && !newBook.getAuthors().isEmpty()) {
                                List<Author> managedAuthors = new ArrayList<>();
                                for (Author author : newBook.getAuthors()) {
                                    // Normalizar a mayúsculas y eliminar comas
                                    author.setName(author.getName().toUpperCase().replace(",", ""));
                                    Optional<Author> existingAuthorOptional = authorRepository.findByName(author.getName());
                                    Author managedAuthor;
                                    if (existingAuthorOptional.isPresent()) {
                                        managedAuthor = existingAuthorOptional.get();
                                    } else {
                                        managedAuthor = authorRepository.save(author);
                                    }
                                    managedAuthors.add(managedAuthor);
                                }
                                newBook.setAuthors(managedAuthors);
                                repository.save(newBook);
                                formatBook(newBook);
                            } else {
                                System.out.println("El libro no tiene autores asignados, no se puede guardar.");
                            }
                        });
            } else {
                System.out.println("No se encontró ningún libro con el nombre '" + nameBook + "'.");
            }
        }
    }



    @Transactional
    private void getAllBooks() {
        var books = repository.findAll();
        for (Book book : books) {
            formatBook(book);
        }
    }

    private List<Author> getAllAuthors() {
        var authors = authorRepository.getAllAuthors();
        for (Author autor : authors) {
            if (autor.getName() != null && !autor.getName().isEmpty()) {
                formatAuthor(autor);
            }
        }
        return authors;
    }

    private List<Author> getAllAuthorsAliveAfter1925() {
        var authorsAlive = authorRepository.getAllAuthorsAliveAfter1925();
        for (Author autor : authorsAlive) {
            if (autor.getName() != null && !autor.getName().isEmpty()) {
                formatAuthor(autor);
            }
        }
        return authorsAlive;
    }
   private List<Book> findBookByLanguage() {
       System.out.println("Escribe 'es' si deseas ver la lista de libros en español o 'en' si deseas ver la lista de libros en inglés");
       var idioma = scanner.nextLine().toLowerCase();
       var booksSpanish = repository.findBookByLanguage(idioma);
        for(Book book :booksSpanish){
            if(book.getLanguages() != null && !book.getLanguages().isEmpty()){
            formatBook(book);
            }
        }

       // Obtener estadísticas sobre el campo download_count
           DoubleSummaryStatistics stats = booksSpanish.stream()
                   .filter(book -> book.getDownload() > 0)
                   .collect(Collectors.summarizingDouble(Book::getDownload));
       System.out.println("Cantidad media de descargas: " + stats.getAverage());
       System.out.println("Cantidad máxima de descargas: " + stats.getMax());
       System.out.println("Cantidad mínima de descargas: "+ stats.getMin());
       System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " + stats.getCount());

        return booksSpanish;
   }
    private Author findByAuthor() {
        System.out.println("Escribe el nombre del autor:");
        String nameAuthorInput = scanner.nextLine().trim().toUpperCase().replace(",", ""); // Convertir a mayúsculas y eliminar comas
        try {
            Optional<Author> authorOptional = authorRepository.findByName(nameAuthorInput);

            if (authorOptional.isPresent()) {
                Author author = authorOptional.get();
                System.out.println("Autor encontrado:");
                formatAuthor(author); // Mostrar los detalles del autor

                List<Book> books = author.getBooks();
                if (books != null && !books.isEmpty()) {
                    System.out.println("Libros escritos por " + author.getName() + ":");
                    for (Book book : books) {
                        formatBook(book);
                    }
                } else {
                    System.out.println("No se encontraron libros para el autor " + author.getName());
                }
                return author;
            } else {
                System.out.println("No se encontró ningún autor con el nombre '" + nameAuthorInput + "'.");
                return null;
            }
        }catch  (Exception e) {
            System.out.println("Ocurrió un error al buscar el autor: " + e.getMessage());
            return null;
        }
    }

    private void formatBook (Book book){
       System.out.println("-----------------------------");
       System.out.println("Titulo: " + book.getTitle());
       System.out.println("Autores: " + book.getAuthors());
       System.out.println("Idiomas: " + book.getLanguages());
       System.out.println("Número de Descargas: " + book.getDownload());
       System.out.println("Genero: " + book.getSubjects());
       System.out.println("-----------------------------");
   }
   private void formatAuthor(Author author){
       System.out.println("------------------------");
       System.out.println("Autor: " + author.getName());
       System.out.println("Fecha de Nacimiento: " + author.getBirthYear());
       System.out.println("Fecha de muerte: " + author.getDeathYear());
       System.out.println("------------------------");
   }
}

