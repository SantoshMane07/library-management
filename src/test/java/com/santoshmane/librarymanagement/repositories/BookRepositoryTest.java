package com.santoshmane.librarymanagement.repositories;

import com.santoshmane.librarymanagement.TestContainerConfiguration;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testFindByPublishDateAfter_whenDateIsInThePast_thenReturnBooksPublishedAfter() {
        // Arrange, Given
        Book book1 = new Book();
        book1.setTitle("Future Book");
        book1.setPublishDate(LocalDate.now().plusDays(10));
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Past Book");
        book2.setPublishDate(LocalDate.now().minusDays(10));
        bookRepository.save(book2);

        // Act, When
        List<Book> books = bookRepository.findByPublishDateAfter(LocalDate.now());

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Future Book");
    }

    @Test
    void testFindByCreatedBy_whenAuthorIsPresent_thenReturnBooksByAuthor() {
        // Arrange, Given
        Author author = new Author();
        author.setName("John Doe");
        authorRepository.save(author);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book by John Doe");
        book1.setCreatedBy(author);
        bookRepository.save(book1);

        // Act, When
        List<Book> books = bookRepository.findByCreatedBy(author);

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getCreatedBy().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindByTitle_whenTitleIsPresent_thenReturnBooksWithTitle() {
        // Arrange, Given
        Book book1 = new Book();
        book1.setTitle("Unique Title");
        bookRepository.save(book1);

        // Act, When
        List<Book> books = bookRepository.findByTitle("Unique Title");

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Unique Title");
    }

    @Test
    void testFindByTitle_whenTitleIsNotFound_thenReturnEmptyBookList() {
        // Act, When
        List<Book> books = bookRepository.findByTitle("Nonexistent Title");

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isEmpty();
    }

    @Test
    void testFindByPublishDateAfter_whenNoBooksAfterDate_thenReturnEmptyBookList() {
        // Arrange, Given
        Book book = new Book();
        book.setTitle("Past Book");
        book.setPublishDate(LocalDate.now().minusDays(10));
        bookRepository.save(book);

        // Act, When
        List<Book> books = bookRepository.findByPublishDateAfter(LocalDate.now());

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isEmpty();
    }

    @Test
    void testFindByCreatedBy_whenAuthorIsNotPresent_thenReturnEmptyBookList() {
        // Arrange, Given
        Author unknownAuthor = new Author();
        unknownAuthor.setName("Unknown Author");
        authorRepository.save(unknownAuthor);
        // Act, When
        List<Book> books = bookRepository.findByCreatedBy(unknownAuthor);

        // Assert, Then
        assertThat(books).isNotNull();
        assertThat(books).isEmpty();
    }

}