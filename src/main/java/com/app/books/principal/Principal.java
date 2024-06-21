package com.app.books.principal;


import com.app.books.model.Book;
import com.app.books.model.Data;
import com.app.books.model.DatosBook;
import com.app.books.repository.BookRepository;
import com.app.books.service.BookConverter;
import com.app.books.service.ConsumoApi;
import com.app.books.service.ConvierteDatos;

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
                    0- Salir
                    """;
            System.out.println(menu);
            opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    searchBook();
                    break;
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
                        System.out.println("Libro guardado correctamente: " + book.toString());
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


}

