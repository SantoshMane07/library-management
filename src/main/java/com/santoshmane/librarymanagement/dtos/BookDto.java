package com.santoshmane.librarymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.santoshmane.librarymanagement.entities.Author;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookDto {

    private Long id;
    @NotBlank(message = "Title of book must not be Blank after trim")
    @Size(min = 3, message = "Name of the book must be at least 3 characters")
    private String title;
    @NotBlank(message = "Description of book must not be Blank after trim")
    private String description;
    private AuthorDto createdBy;
    @PastOrPresent(message = "Book publish date should be Past or Present")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(id, bookDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
