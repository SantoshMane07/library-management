package com.santoshmane.librarymanagement.controllers;
import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.dtos.BookDto;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.entities.Book;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BookControllerTestIT extends AbstractIntegrationTest{

    @Test
    void testGetAllBooks_whenBooksExist_thenReturnBookList() {
        Book book = bookRepository.save(mockBook);
        webTestClient.get()
                .uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].id").isEqualTo(book.getId())
                .jsonPath("$.data.[0].title").isEqualTo(book.getTitle());
    }

    @Test
    void testGetAllBooks_whenBooksNotPresent_thenReturnEmptyList(){
        webTestClient.get()
                .uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isEmpty();
    }

    @Test
    void testGetBookById_whenBookExists_thenReturnBook(){
        Book book = bookRepository.save(mockBook);
        webTestClient.get()
                .uri("/books/{id}",book.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(book.getId());
    }

    @Test
    void testGetBookById_whenBookNotPresent_thenThrowResourceNotFoundError(){

        webTestClient.get()
                .uri("/books/{id}",100)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Book not found by id:"+100);
    }

    @Test
    void testCreateNewBook_whenValidDto_thenReturnCreatedBook() {

        webTestClient.post()
                .uri("/books")
                .bodyValue(mockBookDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.title").isEqualTo(mockBook.getTitle());
    }

    @Test
    void testCreateNewBook_whenPublishedDateIsNotPastOrPresent_thenThrowMethodArgumentError() {

        mockBookDto.setPublishDate(LocalDate.now().plusDays(10));
        webTestClient.post()
                .uri("/books")
                .bodyValue(mockBookDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.apiError.subErrors[0]").isEqualTo("Book publish date should be Past or Present");
    }

    @Test
    void testUpdateBookById_whenValidDto_thenReturnUpdatedBook() {
        Book savedBook = bookRepository.save(mockBook);
        BookDto updatedBookDto = BookDto.builder().title("UPDATED TITLE").description("UPDATED DESCRIPTION").publishDate(LocalDate.now().minusDays(2)).build();

        webTestClient.put()
                .uri("/books/{id}", savedBook.getId())
                .bodyValue(updatedBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.title").isEqualTo(updatedBookDto.getTitle());
    }

    @Test
    void testUpdateBookById_whenBookNotFound_thenThrowResourceNotFoundError() {

        BookDto updatedBookDto = BookDto.builder().title("UPDATED TITLE").description("UPDATED DESCRIPTION").publishDate(LocalDate.now().minusDays(2)).build();

        webTestClient.put()
                .uri("/books/{id}", 100)
                .bodyValue(updatedBookDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Book not found by id:"+100);
    }

    @Test
    void testDeleteBookById_whenValidId_thenDeleteBook() {
        Book savedBook = bookRepository.save(mockBook);

        webTestClient.delete()
                .uri("/books/{id}", savedBook.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteBookById_whenBookNotPresent_thenThrowResourceNotFoundError() {

        webTestClient.delete()
                .uri("/books/{id}", 100)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Book not found by id:"+100);
    }

    @Test
    void testGetBooksPublishedAfterDate_whenBooksExist_thenReturnBooksAfterDate() {
        Book book1 = bookRepository.save(Book.builder().title("BOOK 1").publishDate(LocalDate.of(2023, 1, 1)).build());
        Book book2 = bookRepository.save(Book.builder().title("BOOK 2").publishDate(LocalDate.of(2024, 1, 1)).build());

        webTestClient.get()
                .uri("/books/getAfterDate/{date}", LocalDate.of(2023, 12, 31))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].title").isEqualTo("BOOK 2");
    }

    @Test
    void testGetBooksByTitle_whenBooksExist_thenReturnBooks() {

        Book savedBook = bookRepository.save(mockBook);
        webTestClient.get()
                .uri("/books/title/{title}", savedBook.getTitle())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].title").isEqualTo(savedBook.getTitle());
    }

    @Test
    void testGetBooksByTitle_whenBooksNotPresent_thenReturnEmptyListOfBooks() {

        webTestClient.get()
                .uri("/books/title/{title}", "FIRST BOOK")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isEmpty();
    }

    @Test
    void testGetBooksByCreatedBy_whenBooksPresent_thenReturnBooks() {
        Author savedAuthor = authorRepository.save(mockAuthor);
        mockBook.setCreatedBy(savedAuthor);
        Book savedBook = bookRepository.save(mockBook);
        webTestClient.get()
                .uri("/books/createdBy/{authorId}", savedAuthor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].createdBy.name").isEqualTo(savedAuthor.getName());
    }

    @Test
    void testGetBooksByCreatedBy_whenAuthorNotFound_thenThrowResourceNotFoundError() {
        Author savedAuthor = authorRepository.save(mockAuthor);
        mockBook.setCreatedBy(savedAuthor);
        Book savedBook = bookRepository.save(mockBook);
        webTestClient.get()
                .uri("/books/createdBy/{authorId}", 100)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Author not found by id:"+100);
    }

    @Test
    void testAssignAuthorToBook_whenAuthorAndBookBothPresent_thenReturnBookWithAuthor(){
        Author savedAuthor = authorRepository.save(mockAuthor);
        mockBook.setCreatedBy(savedAuthor);
        Book savedBook = bookRepository.save(mockBook);

        webTestClient.put()
                .uri("/books/{bookId}/assignAuthorToBook/{authorId}",savedBook.getId(),savedAuthor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(savedBook.getId())
                .jsonPath("$.data.createdBy.id").isEqualTo(savedAuthor.getId());
    }

    @Test
    void testAssignAuthorToBook_whenAuthorNotPresent_thenReturnRsourceNotFoundError(){
        Author savedAuthor = authorRepository.save(mockAuthor);
        mockBook.setCreatedBy(savedAuthor);
        Book savedBook = bookRepository.save(mockBook);

        webTestClient.put()
                .uri("/books/{bookId}/assignAuthorToBook/{authorId}",savedBook.getId(),100)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Author not found by id:"+100);

    }

    @Test
    void testAssignAuthorToBook_whenBookNotPresent_thenReturnRsourceNotFoundError(){
        Author savedAuthor = authorRepository.save(mockAuthor);
        mockBook.setCreatedBy(savedAuthor);
        Book savedBook = bookRepository.save(mockBook);

        webTestClient.put()
                .uri("/books/{bookId}/assignAuthorToBook/{authorId}",100,savedAuthor.getId())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.apiError.message").isEqualTo("Book not found by id:"+100);

    }

}

