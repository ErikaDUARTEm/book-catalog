package com.app.books.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;

    @ManyToMany(mappedBy = "books",cascade = { CascadeType.ALL })
    @JsonProperty("authors")
    private List<Author> authors = new ArrayList<>();

    private List<String> languages = new ArrayList<>();
    @JsonProperty("download_count")
    private Long download;

    private List<String> subjects = new ArrayList<>();

    public Book() {

    }
    public Book(DatosBook dataBook){
        this.title = dataBook.getTitle();
        if (dataBook.getAuthors() != null && !dataBook.getAuthors().isEmpty()) {
            this.authors = dataBook.getAuthors(); // O maneja una lista de autores si corresponde
        }
        this.languages = dataBook.getLanguages();
        this.download = dataBook.getDownload();
        this.subjects = dataBook.getSubjects();
    }

    @Override
    public String toString() {
        return
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", languages=" + languages +
                ", download=" + download +
                ", subjects=" + subjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }


    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    public Long getDownload() {
        return download;
    }

    public void setDownload(Long download) {
        this.download = download;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}
