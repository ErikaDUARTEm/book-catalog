package com.app.books.principal;


import com.app.books.model.Book;
import com.app.books.model.DataBook;
import com.app.books.service.ConsumoApi;
import com.app.books.service.ConvierteDatos;

import java.util.Scanner;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "/books";
    private ConvierteDatos conversor = new ConvierteDatos();

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
                    searchForBooks();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void searchForBooks() {
      DataBook datos = getDatosBook();
      Book book = new Book(datos);

    }
    private DataBook getDatosBook(){
        System.out.println("Escribe el libro que deseas buscar");
        var nameBook = scanner.nextLine();
        var json = consumoApi.getDatos(URL_BASE + nameBook.replace(" ", "+"));
        DataBook datos = conversor.getDatos(json, DataBook.class);
        return datos;
    }
}

