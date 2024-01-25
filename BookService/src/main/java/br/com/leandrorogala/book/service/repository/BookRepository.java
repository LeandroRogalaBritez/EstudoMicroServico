package br.com.leandrorogala.book.service.repository;

import br.com.erudio.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>{}