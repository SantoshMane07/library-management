package com.santoshmane.librarymanagement.repositories;

import com.santoshmane.librarymanagement.entities.Author;
import com.santoshmane.librarymanagement.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByPublishDateAfter(LocalDate date);
    List<Book> findByCreatedBy(Author author);
    List<Book> findByTitle(String title);
}
