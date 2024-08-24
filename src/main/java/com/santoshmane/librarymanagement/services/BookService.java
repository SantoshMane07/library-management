package com.santoshmane.librarymanagement.services;

import com.santoshmane.librarymanagement.dtos.BookDto;

import com.santoshmane.librarymanagement.entities.*;
import com.santoshmane.librarymanagement.entities.Book;
import com.santoshmane.librarymanagement.exceptions.ResourceNotFoundException;
import com.santoshmane.librarymanagement.repositories.AuthorRepository;
import com.santoshmane.librarymanagement.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    public List<BookDto> getAllBooks() {
        log.info("Fetching all books");
        List<Book> books = bookRepository.findAll();
        log.info("Successfully fetched all books");
        return books.stream().map((element) -> modelMapper.map(element, BookDto.class)).collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        log.info("Fetching book by id: {}",id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->{
                    log.error("Book not found by id: {}",id);
                    return new ResourceNotFoundException("Book not found by id:"+id);
                });
        return modelMapper.map(book, BookDto.class);
    }   

    public BookDto createNewBook(BookDto bookDto) {
        log.info("Creating new book with title: {}",bookDto.getTitle());
        bookDto.setTitle(bookDto.getTitle().toUpperCase());
        Book savedBook = bookRepository.save(modelMapper.map(bookDto,Book.class));
        log.info("Successfully created new book with title: {}",savedBook.getTitle());
        return modelMapper.map(savedBook, BookDto.class);
    }

    public BookDto updateBookById(Long id,BookDto bookDto) {
        log.info("Updating book by id: {}",id);
        boolean isExists = bookRepository.existsById(id);
        if (!isExists){
            log.error("Book not found by id: {}",id);
            throw new ResourceNotFoundException("Book not found by id:"+id);
        }
        bookDto.setId(id);
        bookDto.setTitle(bookDto.getTitle().toUpperCase());
        log.info("Successfully updated book by id: {}",id);
        return modelMapper.map(bookRepository.save(modelMapper.map(bookDto, Book.class)), BookDto.class);
    }

    public void deleteBookById(Long id) {
        log.info("Deleting book by id: {}",id);
        boolean isExists = bookRepository.existsById(id);
        if (!isExists){
            log.error("Book not found by id: {}",id);
            throw new ResourceNotFoundException("Book not found by id:"+id);
        }
        log.info("Successfully Deleted book by id: {}",id);
        bookRepository.deleteById(id);
    }

    public List<BookDto> getBooksPublishedAfterDate(LocalDate date) {
        log.info("Fetching books published after data: {}",date);
        List<Book> books = bookRepository.findByPublishDateAfter(date);
        log.info("Successfully fetched books published after date: {}",date);
        return books.stream().map((element) -> modelMapper.map(element, BookDto.class)).collect(Collectors.toList());
    }

    public List<BookDto> getBooksByTitle(String title) {
        log.info("Fetching books by title: {}",title);
        List<Book> books = bookRepository.findByTitle(title.toUpperCase());
        log.info("Successfully fetched books by title: {}",title);
        return books.stream().map((element) -> modelMapper.map(element, BookDto.class)).collect(Collectors.toList());
    }

    public List<BookDto> getBooksCreatedBy(Long authorId) {
        log.info("Fetching books created by author id: {}",authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.error("Author not found by id: {}",authorId);
                    return new ResourceNotFoundException("Author not found by id:"+authorId);
                });
        List<Book> books = bookRepository.findByCreatedBy(author);
        log.info("Successfully fetched books by author id: {}",authorId);
        return books.stream().map((element) -> modelMapper.map(element, BookDto.class)).collect(Collectors.toList());
    }

    public BookDto assignAuthorToBook(Long bookId,Long authorId){
        log.info("Assigning author with id: {} to book with id: {} ",authorId,bookId);
        // Step 1: Retrieve the existing book and author from the database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found by id: {}",bookId);
                    return new ResourceNotFoundException("Book not found by id:"+bookId);
                });

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.error("Author not found by id: {}",authorId);
                    return new ResourceNotFoundException("Author not found by id:"+authorId);
                });

        // Step 2: Assign the author to the book
        book.setCreatedBy(author);

        // Step 3: Save the updated book (and relationship) in the database
        Book savedBook = bookRepository.save(book);
        log.info("Successfully Assigned author with id: {} to book with id: {} ",authorId,bookId);
        return modelMapper.map(savedBook,BookDto.class);
    }
}
