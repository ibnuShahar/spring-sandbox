package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
class ReactiveWsR2dbcApplicationTests {
	
	@Autowired
	private ReservationRepository reservationRepository;

	@Test
	void contextLoads() {
		
		Flux<Void> deleteAll = this.reservationRepository.findAll().flatMap(r -> this.reservationRepository.deleteById(r.getId()));
		StepVerifier
			.create(deleteAll)
			.expectNextCount(0)
			.verifyComplete();
		
		Flux<Reservation> saveFlux = Flux.just("first", "second", "third").map(name -> new Reservation(null, name)).flatMap(r -> this.reservationRepository.save(r));
		StepVerifier
			.create(saveFlux)
			.expectNextCount(3)
			.verifyComplete();
		
		Flux<Reservation> getAll = this.reservationRepository.findAll();
		StepVerifier
			.create(getAll)
			.expectNextCount(3)
			.verifyComplete();
		
		
		Flux<Reservation> getOne = this.reservationRepository.findByName("second");
		StepVerifier
			.create(getOne)
			.expectNextCount(1)
			.verifyComplete();
		
		
	}

}
