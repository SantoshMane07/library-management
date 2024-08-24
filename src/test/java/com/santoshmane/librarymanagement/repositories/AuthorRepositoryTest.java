package com.santoshmane.librarymanagement.repositories;

import com.santoshmane.librarymanagement.TestContainerConfiguration;
import com.santoshmane.librarymanagement.entities.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;


@Import(TestContainerConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;


    @Test
    void testFindByName_whenNameIsPresent_thenReturnListOfAuthors() {
        // Arrange, Given
        Author author1 = new Author();
        author1.setName("John Doe");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setName("John Doe");
        authorRepository.save(author2);

        // Act, When
        List<Author> authors = authorRepository.findByName("John Doe");

        // Assert, Then
        assertThat(authors).isNotNull();
        assertThat(authors).isNotEmpty();
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getName).containsOnly("John Doe");
    }

    @Test
    void testFindByName_whenNameIsUnique_thenReturnSingleAuthor() {
        // Arrange, Given
        Author author = new Author();
        author.setName("Jane Doe");
        authorRepository.save(author);

        // Act, When
        List<Author> authors = authorRepository.findByName("Jane Doe");

        // Assert, Then
        assertThat(authors).isNotNull();
        assertThat(authors).isNotEmpty();
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Jane Doe");
    }

    @Test
    void testFindByName_whenNameIsNotFound_thenReturnEmptyAuthorList() {
        // Arrange, Given
        String name = "Unknown Name";

        // Act, When
        List<Author> authors = authorRepository.findByName(name);

        // Assert, Then
        assertThat(authors).isNotNull();
        assertThat(authors).isEmpty();
    }
}