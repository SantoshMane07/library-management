package com.santoshmane.librarymanagement.controllers;
import com.santoshmane.librarymanagement.dtos.BookDto;
import com.santoshmane.librarymanagement.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
@Slf4j
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id){
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> createNewBook(@RequestBody @Valid BookDto bookDto){
        return new ResponseEntity(bookService.createNewBook(bookDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBookById(@RequestBody @Valid BookDto bookDto,@PathVariable Long id){
        return ResponseEntity.ok(bookService.updateBookById(id,bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id){
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAfterDate/{date}")
    public ResponseEntity<List<BookDto>> getBooksPublishedAfterDate(@PathVariable LocalDate date){
        return ResponseEntity.ok(bookService.getBooksPublishedAfterDate(date));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookDto>> getBooksByTitle(@PathVariable String title){
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    @GetMapping("/createdBy/{authorId}")
    public ResponseEntity<List<BookDto>> getBookByAuthor(@PathVariable Long authorId){
        List<BookDto> bookDtos = bookService.getBooksCreatedBy(authorId);
        log.info("Fetched books:{}",bookDtos);
        return ResponseEntity.ok(bookDtos);
    }

    @PutMapping("{bookId}/assignAuthorToBook/{authorId}")
    public ResponseEntity<BookDto> assignAuthorToBook(@PathVariable Long bookId,@PathVariable Long authorId){
        return ResponseEntity.ok(bookService.assignAuthorToBook(bookId,authorId));
    }
}
