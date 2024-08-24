package com.santoshmane.librarymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santoshmane.librarymanagement.entities.Book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthorDto {

    private Long id;
    @NotBlank(message = "Name of the author must not be Blank after trim")
    @Size(min = 3, message = "Name of the author must be at least 3 characters")
    private String name;
    @JsonIgnore
    private List<Book> books;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorDto authorDto = (AuthorDto) o;
        return Objects.equals(id, authorDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
