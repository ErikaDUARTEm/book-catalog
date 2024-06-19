package com.app.books.service;

public interface IConvierteDatos {
    <T> T getDatos(String json, Class<T> clase);
}
