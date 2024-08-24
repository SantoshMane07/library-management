package com.santoshmane.librarymanagement.controllers;


import com.santoshmane.librarymanagement.TestContainerConfiguration;
import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.dtos.BookDto;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.entities.Book;
import com.santoshmane.librarymanagement.repositories.AuthorRepository;
import com.santoshmane.librarymanagement.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@AutoConfigureWebTestClient(timeout = "100000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
public class AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    Author mockAuthor;
    Book mockBook;
    AuthorDto mockAuthorDto;
    BookDto mockBookDto;

    @BeforeEach
    void setUp() {
        mockAuthor = Author.builder()
                .id(1L)
                .name("JOHN DOE")
                .build();

        mockAuthorDto = modelMapper.map(mockAuthor,AuthorDto.class);

        mockBook = Book.builder()
                .id(1L)
                .title("FIRST BOOK")
                .description("First description")
                .publishDate(LocalDate.now())
                .build();
        mockBookDto = modelMapper.map(mockBook, BookDto.class);

        authorRepository.deleteAll();
        bookRepository.deleteAll();
    }

}
