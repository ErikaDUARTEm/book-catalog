package com.app.books.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.ElementCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataBook (
        @JsonAlias("title") String title,
        @JsonAlias("authors") List<Author> author,
        @JsonAlias("languages") List<String> languages,
        @JsonAlias("download_count") Long download,
        @JsonAlias("subjects") List<String> subjects

        ){

}
