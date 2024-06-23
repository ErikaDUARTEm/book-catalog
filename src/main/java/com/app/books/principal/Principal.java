package com.app.books.principal;


import com.app.books.model.Author;
import com.app.books.model.Book;
import com.app.books.model.Data;
import com.app.books.model.DatosBook;
import com.app.books.repository.BookRepository;
import com.app.books.service.BookConverter;
import com.app.books.service.ConsumoApi;
import com.app.books.service.ConvierteDatos;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private BookRepository repository;

    public Principal(BookRepository repository) {

        this.repository = repository;

    }

    public void viewMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1- Buscar libro
                    2- Listar libros buscados
                    3- Listar Autores
                    0- Salir
                    """;
            System.out.println(menu);
            opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    searchBook();
                    break;
                case 2:
                    getAllBooks();
                    break;
                case 3:
                    getAllAuthors();
                case 0:
                    System.out.println("Cerrando la aplicación");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void searchBook(){
        var books = getDatosBook();
        List<DatosBook> datosBooks = BookConverter.convertToDatosBookList(books);
        datosBooks.stream()
                .findFirst()
                .ifPresent(datosBook -> {
                    Book book = new Book(datosBook);

                    // Verifica que la lista de autores no sea nula ni esté vacía
                    if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {

                        repository.save(book);
                        System.out.println("Libro guardado correctamente: " + book.getTitle());
                        System.out.println(book.getAuthors());
                    } else {
                        System.out.println("El libro no tiene autores asignados, no se puede guardar.");
                        // Aquí puedes manejar la lógica adicional si es necesario
                    }


                });
    }
    private Data getDatosBook(){
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nameBook = scanner.nextLine();
        var json = consumoApi.getDatos(URL_BASE + "?search=" + nameBook.replace(" ", "+"));
        Data datos = conversor.getDatos(json, Data.class);
        return datos;
    }
    @Transactional
    private void getAllBooks(){

        var  books = repository.getAllBooks();
        for (Book book : books) {
            System.out.println("Título: " + book.getTitle());

            // Mostrar autores

                System.out.print("Autores: " + book.getAuthors());

            System.out.println();

            System.out.println("Idiomas: " + book.getLanguages());
            System.out.println("Descargas: " + book.getDownload());
            System.out.println("Temas: " + book.getSubjects());
            System.out.println("-----------------------------");
        }
    }
    private List<Author> getAllAuthors(){
        var authors = repository.getAllAuthors();
        for(Author autor : authors){
            if(autor.getName()  != null && !autor.getName().isEmpty()){
                System.out.println("Autor: " + autor.getName());
                System.out.println("Fecha de Nacimiento: " + autor.getBirthYear());
                System.out.println("Fecha de muerte: "+ autor.getDeathYear());
                System.out.println("------------------------");
            }
        }
        return authors;
    }

}

