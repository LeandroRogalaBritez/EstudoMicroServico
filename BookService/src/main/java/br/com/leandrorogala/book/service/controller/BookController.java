package br.com.leandrorogala.book.service.controller;

import br.com.leandrorogala.book.service.model.Book;
import br.com.leandrorogala.book.service.proxy.CambioProxy;
import br.com.leandrorogala.book.service.repository.BookRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {
	private Logger logger = LoggerFactory.getLogger(BookController.class);
	@Autowired
	private Environment environment;
	@Autowired
	private BookRepository repository;
	@Autowired
	private CambioProxy proxy;
	
	@Operation(summary = "Find a specific book by your ID")
	@GetMapping(value = "/{id}/{currency}")
	@Retry(name = "default")
	@RateLimiter(name = "default")
	@Bulkhead(name = "default")
	public Book findBook(@PathVariable("id") Long id, @PathVariable("currency") String currency) {
		logger.info("Request to findBook is received!");

		var book = repository.getById(id);
		if (book == null) throw new RuntimeException("Book not Found");
				
		var cambio = proxy.getCambio(book.getPrice(), "USD", currency);
		
		var port = environment.getProperty("local.server.port");
		book.setEnvironment(
				"Book port: " + port + 
				" Cambio Port " + cambio.getEnvironment());
		book.setPrice(cambio.getConvertedValue());
		return book;
	}

	public String findBookFallBack(Exception ex) {
		return "fallbackMethod!!!";
	}

}
