package com.santoshmane.librarymanagement.services;

import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.exceptions.ResourceNotFoundException;
import com.santoshmane.librarymanagement.repositories.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Spy
    private ModelMapper modelMapper;

    private Author mockAuthor;
    private AuthorDto mockAuthorDto;

    @BeforeEach
    void setUp() {
        mockAuthor = Author.builder()
                .id(1L)
                .name("JOHN DOE")
                .build();

        mockAuthorDto = AuthorDto.builder()
                .id(1L)
                .name("JOHN DOE")
                .build();
    }

    @Test
    void testGetAllAuthors_whenAuthorsArePresent_thenReturnListOfAuthorDtos() {
        // Arrange, Given
        when(authorRepository.findAll()).thenReturn(List.of(mockAuthor));

        // Act, When
        List<AuthorDto> authorDtoList = authorService.getAllAuthors();

        // Assert, Then
        assertThat(authorDtoList).isNotNull();
        assertThat(authorDtoList).hasSize(1);
        assertThat(authorDtoList.get(0).getName()).isEqualTo(mockAuthor.getName());
        verify(authorRepository, only()).findAll();
    }

    @Test
    void testGetAuthorById_whenAuthorIdIsPresent_thenReturnAuthorDto() {
        // Arrange, Given
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(mockAuthor));

        // Act, When
        AuthorDto authorDto = authorService.getAuthorById(1L);

        // Assert, Then
        assertThat(authorDto).isNotNull();
        assertThat(authorDto.getId()).isEqualTo(mockAuthor.getId());
        verify(authorRepository, only()).findById(1L);
    }

    @Test
    void testGetAuthorById_whenAuthorIdIsNotPresent_thenThrowException() {
        // Arrange, Given
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert, When & Then
        assertThatThrownBy(() -> authorService.getAuthorById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author not found by id:1");

        verify(authorRepository).findById(1L);
    }

    @Test
    void testCreateNewAuthor_whenValidAuthor_thenCreateNewAuthor() {
        // Arrange, Given
        when(authorRepository.save(any(Author.class))).thenReturn(mockAuthor);

        // Act, When
        AuthorDto createdAuthorDto = authorService.createNewAuthor(mockAuthorDto);

        // Assert, Then
        assertThat(createdAuthorDto).isNotNull();
        assertThat(createdAuthorDto.getName()).isEqualTo(mockAuthorDto.getName().toUpperCase());

        ArgumentCaptor<Author> authorArgumentCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).save(authorArgumentCaptor.capture());
        assertThat(authorArgumentCaptor.getValue().getName()).isEqualTo(mockAuthor.getName().toUpperCase());
    }

    @Test
    void testUpdateAuthorById_whenAuthorExists_thenUpdateAuthor() {
        // Arrange, Given
        when(authorRepository.existsById(anyLong())).thenReturn(true);
        when(authorRepository.save(any(Author.class))).thenReturn(mockAuthor);

        // Act, When
        AuthorDto updatedAuthorDto = authorService.updateAuthorById(1L, mockAuthorDto);

        // Assert, Then
        assertThat(updatedAuthorDto).isNotNull();
        assertThat(updatedAuthorDto.getId()).isEqualTo(mockAuthor.getId());
        verify(authorRepository).existsById(1L);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void testUpdateAuthorById_whenAuthorDoesNotExist_thenThrowException() {
        // Arrange, Given
        when(authorRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert, When & Then
        assertThatThrownBy(() -> authorService.updateAuthorById(1L, mockAuthorDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author not found by id:1");

        verify(authorRepository).existsById(1L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testDeleteAuthorById_whenAuthorExists_thenDeleteAuthor() {
        // Arrange, Given
        when(authorRepository.existsById(anyLong())).thenReturn(true);

        // Act, When
        authorService.deleteAuthorById(1L);

        // Assert, Then
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void testDeleteAuthorById_whenAuthorDoesNotExist_thenThrowException() {
        // Arrange, Given
        when(authorRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert, When & Then
        assertThatThrownBy(() -> authorService.deleteAuthorById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author not found by id:1");

        verify(authorRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAuthorsByName_whenAuthorsArePresent_thenReturnListOfAuthorDtos() {
        // Arrange, Given
        when(authorRepository.findByName(anyString())).thenReturn(List.of(mockAuthor));

        // Act, When
        List<AuthorDto> authorDtoList = authorService.getAuthorsByName("John Doe");

        // Assert, Then
        assertThat(authorDtoList).isNotNull();
        assertThat(authorDtoList).hasSize(1);
        assertThat(authorDtoList.get(0).getName()).isEqualTo(mockAuthor.getName().toUpperCase());
        verify(authorRepository, only()).findByName("JOHN DOE");
    }
}