package com.papa.bible.bean;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

/**
 * Created by rodrigo.almeida on 30/04/15.
 */
public class BookDecompressed {
    private List<String> urlResources;
    private Book book;
    private String baseURL;

    public BookDecompressed() {
        urlResources = new ArrayList<>();
    }

    public void setUrlResources(List<String> urlResources) {
        this.urlResources = urlResources;
    }

    public void setUrlResources(String urlResource) {
        this.urlResources.add(urlResource);
    }

    public List<String> getUrlResources() {
        return urlResources;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
