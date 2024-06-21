package com.app.books.dto;

import com.app.books.model.Author;
import com.app.books.model.DataAuthor;

import java.util.List;


public record BookDTO(
        String title,
        List<DataAuthor> authors,
        List<String> languages,
        Long download,
        List<String> subjects
) {
}
