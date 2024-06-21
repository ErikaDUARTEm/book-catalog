package com.app.books.service;

import com.app.books.model.Data;
import com.app.books.model.DataBook;
import com.app.books.model.DatosBook;

import java.util.ArrayList;
import java.util.List;

public class BookConverter {
    public static List<DatosBook> convertToDatosBookList(Data data) {
        List<DatosBook> datosBooks = new ArrayList<>();
        for (DataBook dataBook : data.resultados()) {
            DatosBook datosBook = new DatosBook();
            datosBook.setTitle(dataBook.title());
            datosBook.setAuthors(dataBook.author());
            datosBook.setLanguages(dataBook.languages());
            datosBook.setDownload(dataBook.download());
            datosBook.setSubjects(dataBook.subjects());
            datosBooks.add(datosBook);
        }
        return datosBooks;
    }
}
