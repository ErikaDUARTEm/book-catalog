package com.app.books.dto;

import com.app.books.model.Author;

import java.util.List;


public record BookDTO(
        String title,
        List<Author> authors,
        List<String> languages,
        Long download,
        List<String> subjects
) {
}
