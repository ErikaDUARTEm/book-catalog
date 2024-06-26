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
import org.springframework.dao.DataIntegrityViolationException;

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
                    5- Listar los libros que están en inglés o español
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
        String nameBook = scanner.nextLine().trim().toLowerCase();

        // Buscar el libro por título en la base de datos
        Book existingBook = repository.findByTitleIgnoreCase(nameBook);
        if (existingBook != null) {
            System.out.println("Libro encontrado en la base de datos:");
            formatBook(existingBook);
            return;
        }

        // Si no existe localmente, buscar en la API
        String apiUrl = URL_BASE + "?search=" + nameBook.replace(" ", "+");
        String json = consumoApi.getDatos(apiUrl);
        Data datos = conversor.getDatos(json, Data.class);

        if (datos != null) {
            List<DatosBook> datosBooks = BookConverter.convertToDatosBookList(datos);
            datosBooks.stream()
                    .findFirst()
                    .ifPresent(datosBook -> {
                        Book newBook = new Book(datosBook);

                        // Verificar y manejar autores asociados
                        if (newBook.getAuthors() != null && !newBook.getAuthors().isEmpty()) {
                            try {
                                repository.save(newBook);
                                System.out.println("Libro guardado exitosamente:");
                                formatBook(newBook);
                            } catch (DataIntegrityViolationException e) {
                                // Manejar el caso de clave duplicada (título duplicado)
                                System.out.println("No se pudo guardar el libro: ya existe un libro con el mismo título.");
                            }
                        } else {
                            System.out.println("El libro encontrado no tiene autores asociados, no se guardará.");
                        }
                    });
        } else {
            System.out.println("No se encontró ningún libro con el nombre '" + nameBook + "'.");
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

