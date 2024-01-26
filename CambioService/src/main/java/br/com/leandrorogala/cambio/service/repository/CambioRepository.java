package br.com.leandrorogala.cambio.service.repository;

import br.com.leandrorogala.cambio.service.model.Cambio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CambioRepository extends JpaRepository<Cambio, Long> {

	Cambio findByFromAndTo(String from, String to);
	
}