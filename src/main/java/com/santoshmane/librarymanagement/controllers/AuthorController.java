package com.santoshmane.librarymanagement.controllers;

import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.services.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/authors")
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors(){
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id){
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createNewAuthor(@RequestBody @Valid AuthorDto authorDto){
        return new ResponseEntity(authorService.createNewAuthor(authorDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthorById(@RequestBody @Valid AuthorDto authorDto,@PathVariable Long id){
        return ResponseEntity.ok(authorService.updateAuthorById(id,authorDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthorById(@PathVariable Long id){
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<AuthorDto>> getAuthorsByName(@PathVariable String name){
        return ResponseEntity.ok(authorService.getAuthorsByName(name));
    }
}
