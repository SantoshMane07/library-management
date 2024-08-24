package com.santoshmane.librarymanagement.services;

import com.santoshmane.librarymanagement.dtos.AuthorDto;
import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.exceptions.ResourceNotFoundException;
import com.santoshmane.librarymanagement.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    public List<AuthorDto> getAllAuthors() {
        log.info("Fetching all authors");
        List<Author> authors = authorRepository.findAll();
        List<AuthorDto> authorDtoList = authors.stream().map((author) -> modelMapper.map(author, AuthorDto.class))
                .collect(Collectors.toList());
        log.info("Successfully fetched all authors");
        return authorDtoList;
    }

    public AuthorDto getAuthorById(Long id) {
        log.info("Fetching author by id: {}",id);
        Author author = authorRepository.findById(id).orElseThrow(() -> {
            log.error("Author not found by id {}",id);
            return new ResourceNotFoundException("Author not found by id:"+id);
        });
        log.info("Successfully fetched author by id: {}",id);
        return modelMapper.map(author, AuthorDto.class);
    }

    public AuthorDto createNewAuthor(AuthorDto authorDto) {
        log.info("Creating new author by name: {}",authorDto.getName());
        authorDto.setName(authorDto.getName().toUpperCase());
        Author savedAuthor = authorRepository.save(modelMapper.map(authorDto,Author.class));
        log.info("Successfully Created new author by name: {}",authorDto.getName());
        return modelMapper.map(savedAuthor, AuthorDto.class);
    }

    public AuthorDto updateAuthorById(Long id,AuthorDto authorDto) {
        log.info("Updating author by id: {}",id);
        boolean isExists = authorRepository.existsById(id);
        if (!isExists){
            log.error("Author not found by id: {}",id);
            throw new ResourceNotFoundException("Author not found by id:"+id);
        }
        authorDto.setId(id);
        authorDto.setName(authorDto.getName().toUpperCase());
        log.info("Successfully Updated author by id: {}",id);
        return modelMapper.map(authorRepository.save(modelMapper.map(authorDto,Author.class)), AuthorDto.class);
    }

    public void deleteAuthorById(Long id) {
        log.info("Deleting author by id: {}",id);
        boolean isExists = authorRepository.existsById(id);
        if (!isExists){
            log.error("Author not found by id: {}",id);
            throw new ResourceNotFoundException("Author not found by id:"+id);
        }
        log.info("Successfully deleted author by id: {}",id);
        authorRepository.deleteById(id);
    }

    public List<AuthorDto> getAuthorsByName(String name) {
        log.info("Fetching authors by name: {}",name);
        List<Author> authors = authorRepository.findByName(name.toUpperCase());
        List<AuthorDto> authorDtoList = authors.stream().map((element) -> modelMapper.map(element, AuthorDto.class)).collect(Collectors.toList());
        log.info("Successfully fetched all authors by name: {}",name);
        return authorDtoList;
    }
}
