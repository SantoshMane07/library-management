package com.santoshmane.librarymanagement.controllers;

import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.entities.Author;
import org.junit.jupiter.api.Test;

class AuthorControllerTestIT extends AbstractIntegrationTest{
    @Test
    void testGetAllAuthors_success() {
        Author savedAuthor = authorRepository.save(mockAuthor);

        webTestClient.get()
                .uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.data.[0].name").isEqualTo(savedAuthor.getName());
    }

    @Test
    void testGetAuthorById_success() {
        Author savedAuthor = authorRepository.save(mockAuthor);

        webTestClient.get()
                .uri("/authors/{id}", savedAuthor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.data.name").isEqualTo(savedAuthor.getName());
    }

    @Test
    void testGetAuthorById_failure() {
        webTestClient.get()
                .uri("/authors/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewAuthor_whenValidDto_thenReturnCreatedAuthor() {


        webTestClient.post()
                .uri("/authors")
                .bodyValue(mockAuthorDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo(mockAuthorDto.getName());
    }

    @Test
    void testCreateNewAuthor_whenInvalidDto_thenReturnBadRequest() {
        AuthorDto invalidAuthorDto = AuthorDto.builder()
                .name("Jo") // Invalid as it has less than 3 characters
                .build();

        webTestClient.post()
                .uri("/authors")
                .bodyValue(invalidAuthorDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.apiError.subErrors[0]").isEqualTo("Name of the author must be at least 3 characters");
    }

    @Test
    void testUpdateAuthorById_whenValidDto_thenReturnUpdatedAuthor() {
        Author savedAuthor = authorRepository.save(mockAuthor);
        AuthorDto updatedAuthorDto = AuthorDto.builder()
                .id(savedAuthor.getId())
                .name("UPDATED NAME")
                .build();

        webTestClient.put()
                .uri("/authors/{id}", savedAuthor.getId())
                .bodyValue(updatedAuthorDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo(updatedAuthorDto.getName());
    }

    @Test
    void testUpdateAuthorById_whenInvalidDto_thenReturnBadRequest() {
        Author savedAuthor = authorRepository.save(mockAuthor);
        AuthorDto invalidAuthorDto = AuthorDto.builder()
                .id(savedAuthor.getId())
                .name("JO") // Invalid name
                .build();

        webTestClient.put()
                .uri("/authors/{id}", savedAuthor.getId())
                .bodyValue(invalidAuthorDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.apiError.subErrors[0]").isEqualTo("Name of the author must be at least 3 characters");
    }

    @Test
    void testDeleteAuthorById_success() {
        Author savedAuthor = authorRepository.save(mockAuthor);

        webTestClient.delete()
                .uri("/authors/{id}", savedAuthor.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/authors/{id}", savedAuthor.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteAuthorById_failure() {
        webTestClient.delete()
                .uri("/authors/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetAuthorsByName_success() {
        Author savedAuthor = authorRepository.save(mockAuthor);

        webTestClient.get()
                .uri("/authors/name/{name}", savedAuthor.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.[0].id").isEqualTo(savedAuthor.getId())
                .jsonPath("$.data.[0].name").isEqualTo(savedAuthor.getName());
    }

    @Test
    void testGetAuthorsByName_emptyResult() {
        webTestClient.get()
                .uri("/authors/name/NonExistentName")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isEmpty();
    }
}