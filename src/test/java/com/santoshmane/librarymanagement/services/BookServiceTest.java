package com.santoshmane.librarymanagement.services;

import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.dtos.BookDto;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.entities.Book;
import com.santoshmane.librarymanagement.exceptions.ResourceNotFoundException;
import com.santoshmane.librarymanagement.repositories.AuthorRepository;
import com.santoshmane.librarymanagement.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private BookService bookService;
    @Spy
    private ModelMapper modelMapper;

    private Author mockAuthor;
    private Book mockBook;
    private AuthorDto mockAuthorDto;
    private BookDto mockBookDto;

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
                .publishDate(LocalDate.now())
                .build();
        mockBookDto = modelMapper.map(mockBook, BookDto.class);
    }

    @Test
    void testGetAllBooks_whenBooksArePresent_thenReturnListOfBookDtos() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(mockBook));
        // Act
        List<BookDto> bookDtoList = bookService.getAllBooks();

        // Assert
        assertThat(bookDtoList).isNotNull();
        assertThat(bookDtoList).hasSize(1);
        assertThat(bookDtoList.get(0).getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, only()).findAll();
    }

    @Test
    void testGetBookById_whenBookExists_thenReturnBookDto() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        // Act
        BookDto bookDto = bookService.getBookById(bookId);

        // Assert
        assertThat(bookDto).isNotNull();
        assertThat(bookDto.getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, only()).findById(bookId);
    }

    @Test
    void testGetBookById_whenBookDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookId));
        verify(bookRepository, only()).findById(bookId);
    }

    @Test
    void testCreateNewBook_whenValidBookDto_thenReturnSavedBookDto() {
        // Arrange
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // Act
        BookDto savedBookDto = bookService.createNewBook(mockBookDto);

        // Assert
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        assertThat(savedBookDto).isNotNull();
        assertThat(savedBookDto.getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, only()).save(bookArgumentCaptor.capture());
    }

    @Test
    void testUpdateBookById_whenBookExists_thenReturnUpdatedBookDto() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(modelMapper.map(mockBookDto, Book.class)).thenReturn(mockBook);
        when(bookRepository.save(mockBook)).thenReturn(mockBook);
        when(modelMapper.map(mockBook, BookDto.class)).thenReturn(mockBookDto);

        // Act
        BookDto updatedBookDto = bookService.updateBookById(bookId, mockBookDto);

        // Assert
        assertThat(updatedBookDto).isNotNull();
        assertThat(updatedBookDto.getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).save(mockBook);
    }

    @Test
    void testUpdateBookById_whenBookDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBookById(bookId, mockBookDto));
        verify(bookRepository, only()).existsById(bookId);
    }

    @Test
    void testDeleteBookById_whenBookExists_thenBookIsDeleted() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // Act
        bookService.deleteBookById(bookId);

        // Assert
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void testDeleteBookById_whenBookDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBookById(bookId));
        verify(bookRepository, only()).existsById(bookId);
    }

    @Test
    void testGetBooksPublishedAfterDate_whenBooksArePresent_thenReturnListOfBookDtos() {
        // Arrange
        LocalDate date = LocalDate.of(2020, 1, 1);
        when(bookRepository.findByPublishDateAfter(date)).thenReturn(List.of(mockBook));
        when(modelMapper.map(mockBook, BookDto.class)).thenReturn(mockBookDto);

        // Act
        List<BookDto> bookDtoList = bookService.getBooksPublishedAfterDate(date);

        // Assert
        assertThat(bookDtoList).isNotNull();
        assertThat(bookDtoList).hasSize(1);
        assertThat(bookDtoList.get(0).getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, only()).findByPublishDateAfter(date);
    }

    @Test
    void testGetBooksByTitle_whenBooksArePresent_thenReturnListOfBookDtos() {
        // Arrange
        String title = "Test Title";
        when(bookRepository.findByTitle(title.toUpperCase())).thenReturn(List.of(mockBook));

        // Act
        List<BookDto> bookDtoList = bookService.getBooksByTitle(title);

        // Assert
        assertThat(bookDtoList).isNotNull();
        assertThat(bookDtoList).hasSize(1);
        assertThat(bookDtoList.get(0).getTitle()).isEqualTo(mockBook.getTitle());
        verify(bookRepository, only()).findByTitle(title.toUpperCase());
    }

    @Test
    void testGetBooksCreatedBy_whenAuthorExists_thenReturnListOfBookDtos() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));
        when(bookRepository.findByCreatedBy(mockAuthor)).thenReturn(List.of(mockBook));
        when(modelMapper.map(mockBook, BookDto.class)).thenReturn(mockBookDto);

        // Act
        List<BookDto> bookDtoList = bookService.getBooksCreatedBy(authorId);

        // Assert
        assertThat(bookDtoList).isNotNull();
        assertThat(bookDtoList).hasSize(1);
        assertThat(bookDtoList.get(0).getTitle()).isEqualTo(mockBook.getTitle());
        verify(authorRepository, times(1)).findById(authorId);
        verify(bookRepository, times(1)).findByCreatedBy(mockAuthor);
    }

    @Test
    void testGetBooksCreatedBy_whenAuthorDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBooksCreatedBy(authorId));
        verify(authorRepository, only()).findById(authorId);
    }

    @Test
    void testAssignAuthorToBook_whenBookAndAuthorExist_thenReturnUpdatedBookDto() {
        // Arrange
        Long bookId = 1L;
        Long authorId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));
        mockBook.setCreatedBy(mockAuthor);
        when(bookRepository.save(mockBook)).thenReturn(mockBook);

        // Act
        BookDto updatedBookDto = bookService.assignAuthorToBook(bookId, authorId);

        // Assert
        assertThat(updatedBookDto).isNotNull();
        assertThat(updatedBookDto.getTitle()).isEqualTo(mockBook.getTitle());
        assertThat(updatedBookDto.getCreatedBy().getId()).isEqualTo(authorId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(authorRepository, times(1)).findById(authorId);
        verify(bookRepository, times(1)).save(mockBook);
    }

    @Test
    void testAssignAuthorToBook_whenBookDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long bookId = 1L;
        Long authorId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.assignAuthorToBook(bookId, authorId));
        verify(bookRepository, only()).findById(bookId);
    }

    @Test
    void testAssignAuthorToBook_whenAuthorDoesNotExist_thenThrowResourceNotFoundException() {
        // Arrange
        Long bookId = 1L;
        Long authorId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> bookService.assignAuthorToBook(bookId, authorId));
        verify(authorRepository, only()).findById(authorId);
    }








}