package com.app.books.model;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DatosBook {
    private String title;
    private List<Author> authors = new ArrayList<>();

    private List<String> languages = new ArrayList<>();
    private Long download;

    private List<String> subjects = new ArrayList<>();
    private Map<String, String> formats;

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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(title).append("\n");
        sb.append("Authors: ");
        for (Author author : authors) {
            sb.append(author.getName()).append(" (").append(author.getBirthYear()).append(" - ").append(author.getDeathYear()).append("), ");
        }
        sb.append("\n");
        sb.append("Languages: ").append(String.join(", ", languages)).append("\n");
        sb.append("Downloads: ").append(download).append("\n");
        sb.append("Subjects: ").append(String.join(", ", subjects)).append("\n");
        return sb.toString();
    }
}
